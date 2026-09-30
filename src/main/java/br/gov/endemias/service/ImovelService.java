package br.gov.endemias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.dto.ImovelRequest;
import br.gov.endemias.dto.ImovelResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.ImovelRepository;
import br.gov.endemias.repository.LadoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImovelService {
    
    private final ImovelRepository imovelRepository;
    private final LadoRepository ladoRepository;
    private final LocalidadeService localidadeService;

    @Transactional
    public ImovelResponse cadastrar(ImovelRequest request) {

        validarExclusividadeLadoELocalidade(request.ladoId(), request.localidadeId());

        Imovel imovel = request.toEntity();
        
        atribuirLadoOuLocalidade(imovel, request.ladoId(), request.localidadeId());

        processarNumeroSms(imovel, request.placa(), request.numeroSms(), request.ladoId(), request.localidadeId());
        processarPlacaESequencia(imovel, request.placa(), request.ladoId(), request.localidadeId(), null);

        if (request.ordem() != null) {
            imovel.setOrdem(request.ordem());
            if (request.ladoId() != null) {
                imovelRepository.reordenarImoveisNoLado(request.ladoId(), request.ordem());
            } else {
                imovelRepository.reordenarImoveisNaLocalidade(request.localidadeId(), request.ordem());
            }
        } else {
            Integer ultimaOrdem = request.ladoId() != null
                ? imovelRepository.findFirstByLadoIdOrderByOrdemDesc(request.ladoId())
                    .map(Imovel::getOrdem).orElse(0)
                : imovelRepository.findFirstByLocalidadeIdOrderByOrdemDesc(request.localidadeId())
                    .map(Imovel::getOrdem).orElse(0);
            imovel.setOrdem(ultimaOrdem + 1);
        }

        return ImovelResponse.fromEntity(imovelRepository.save(imovel));
    }

    public List<ImovelResponse> listarPorLado(Long ladoId) {
        return imovelRepository.findAllByLadoIdOrderByOrdemAsc(ladoId)
            .stream().map(ImovelResponse::fromEntity).toList();
    }

    public List<Imovel> listarPorLadoIdIn(List<Long> ladoIdList) {
        return imovelRepository.findAllByLadoIdIn(ladoIdList);
    }

    public ImovelResponse buscarPorId(Long id) {
        Imovel imovel = buscarEntityPorId(id);
        return ImovelResponse.fromEntity(imovel);
    }

    public ImovelResponse atualizar(Long id, ImovelRequest request) {
        Imovel imovel = buscarEntityPorId(id);
        String placaAntiga = imovel.getPlaca();

        Long ladoId = request.ladoId() != null ? request.ladoId() : ( imovel.getLado() != null ? imovel.getLado().getId() : null);
        Long localidadeId = request.localidadeId() != null ? request.localidadeId() : ( imovel.getLocalidade() != null ? imovel.getLocalidade().getId() : null);
        
        validarExclusividadeLadoELocalidade(ladoId, localidadeId);
        atribuirLadoOuLocalidade(imovel, request.ladoId(), request.localidadeId());

        processarNumeroSms(imovel, request.placa(), request.numeroSms(), ladoId, localidadeId);
        processarPlacaESequencia(imovel, request.placa(), ladoId, localidadeId, placaAntiga);
        
        imovel.setNumeroSms(request.numeroSms() != null ? request.numeroSms() : imovel.getNumeroSms());
        imovel.setPlaca(request.placa() != null ? request.placa() : imovel.getPlaca());
        imovel.setSequencia(request.sequencia() != null ? request.sequencia() : imovel.getSequencia());
        imovel.setNumeroResidentes(request.numeroResidentes() != null ? request.numeroResidentes() : imovel.getNumeroResidentes());
        imovel.setNumeroCaes(request.numeroCaes() != null ? request.numeroCaes() : imovel.getNumeroCaes());
        imovel.setNumeroGatos(request.numeroGatos() != null ? request.numeroGatos() : imovel.getNumeroGatos());
        imovel.setTipo(request.tipo() != null ? request.tipo() : imovel.getTipo());

        return ImovelResponse.fromEntity(imovelRepository.save(imovel));
    }

    public void deletar(Long id) {
        imovelRepository.deleteById(id);
    }

    public Imovel buscarEntityPorId(Long id) {
        return imovelRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Imovel não encontrado")
            );
    }

    private void validarExclusividadeLadoELocalidade(Long ladoId, Long localidadeId) {
        if ((ladoId == null && localidadeId == null) || (ladoId != null && localidadeId != null)) {
            throw new RegraNegocioException("O imóvel precisa ter um lado ou uma localidade (e apenas um deles).");
        }
    }

    private void atribuirLadoOuLocalidade(Imovel imovel, Long ladoId, Long localidadeId) {
        if (ladoId != null) {
            imovel.setLado(ladoRepository.findById(ladoId)
                .orElseThrow(() -> new ResourceNotFoundException("Lado nao encontrado")));
            imovel.setLocalidade(null);
        } else if (localidadeId != null) {
            imovel.setLocalidade(localidadeService.buscarEntityPorId(localidadeId));
            imovel.setLado(null);
        }
    }

    @Transactional
    private void processarNumeroSms(Imovel imovel, String placa, Integer numeroSmsRequest, Long ladoId, Long localidadeId) {
        if (placa == null && numeroSmsRequest == null) {
            if (imovel.getNumeroSms() == null) {
                Integer novoNumeroSms = 0;
                if (ladoId != null) {
                    novoNumeroSms = imovelRepository
                        .findFirstByLadoIdOrderByNumeroSmsDesc(imovel.getLado().getId())
                        .map(imovelEntity -> imovelEntity.getNumeroSms())
                        .orElse(0) + 1;
                    imovel.setNumeroSms(novoNumeroSms);
                } else {
                    novoNumeroSms = imovelRepository
                        .findFirstByLocalidadeIdOrderByNumeroSmsDesc(imovel.getLocalidade().getId())
                        .map(imovelEntity -> imovelEntity.getNumeroSms())
                        .orElse(0) + 1;
                }

                imovel.setNumeroSms(novoNumeroSms);
            }
        } else {
            if (numeroSmsRequest != null) {
                if (ladoId != null) {
                    imovelRepository.abrirEspacoParaNovoImovel(ladoId, numeroSmsRequest);
                } else {
                    imovelRepository.abrirEspacoParaNovoImovelNaLocalidade(localidadeId, numeroSmsRequest);
                }
            }
            imovel.setNumeroSms(numeroSmsRequest);
        }
    }

    private void processarPlacaESequencia(Imovel imovel, String placaRequest, Long ladoId, Long localidadeId, String placaAntiga) {
        if (placaRequest != null) {
            imovel.setPlaca(placaRequest);

            boolean placaMudou = !placaRequest.equals(placaAntiga);

            if (placaMudou) {
                Integer sequencia = (ladoId != null
                    ? imovelRepository.findFirstByPlacaAndLadoIdOrderBySequenciaDesc(placaRequest, ladoId)
                    : imovelRepository.findFirstByPlacaAndLocalidadeIdOrderBySequenciaDesc(placaRequest, localidadeId))
                    .map(ultimo -> ultimo.getSequencia() == null ? 1 : ultimo.getSequencia() + 1)
                    .orElse(0);
                imovel.setSequencia(sequencia);
            }
        } else {
            imovel.setPlaca(null);
            imovel.setSequencia(null);
        }
    }

}
