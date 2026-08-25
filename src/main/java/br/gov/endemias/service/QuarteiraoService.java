package br.gov.endemias.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Area;
import br.gov.endemias.domain.entity.Localidade;
import br.gov.endemias.domain.entity.Quarteirao;
import br.gov.endemias.dto.LadoDetalhadoResponse;
import br.gov.endemias.dto.QuarteiraoDetalhadoResponse;
import br.gov.endemias.dto.QuarteiraoRequest;
import br.gov.endemias.dto.QuarteiraoResponse;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.QuarteiraoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuarteiraoService {
    
    private final QuarteiraoRepository quarteiraoRepository;
    private final LocalidadeService localidadeService;
    private final AreaService areaService;
    private final LadoService ladoService;

    public QuarteiraoResponse cadastrar(QuarteiraoRequest request) {

        Quarteirao quarteirao = request.toEntity();

        if (request.numero() != null) {
            boolean jaExiste = quarteiraoRepository.existsByNumeroAndLocalidadeId(
                    request.numero(),
                    request.localidadeId()
                );

            if (jaExiste) {
                int maiorSequenciaAtual = quarteiraoRepository
                        .findFirstByNumeroAndLocalidadeIdOrderBySequenciaDesc(
                            request.numero(),
                            request.localidadeId()
                        )
                        .map(q -> q.getSequencia() != null ? q.getSequencia() : 0)
                        .orElse(0);

                quarteirao.setSequencia(maiorSequenciaAtual + 1);
            } else {
                quarteirao.setSequencia(0);
            }
        }

        if (request.localidadeId() != null) {
            Localidade localidade = localidadeService.buscarEntityPorId(request.localidadeId());
            quarteirao.setLocalidade(localidade);
        }

        if (request.areaId() != null) {
            Area area = areaService.buscarEntityPorId(request.areaId());
            quarteirao.setArea(area);
        }

        Quarteirao quarteiraoSalvo = quarteiraoRepository.save(quarteirao);
        return QuarteiraoResponse.fromEntity(quarteiraoSalvo);
    }

    public List<QuarteiraoResponse> listarPorLocalidade(Long localidadeId) {
        return quarteiraoRepository.findAllByLocalidadeId(localidadeId);
    }

    public QuarteiraoResponse buscarPorId(Long id) {
        Quarteirao quarteirao = buscarEntityById(id);
        return QuarteiraoResponse.fromEntity(quarteirao);
    }

    public QuarteiraoDetalhadoResponse buscarDetalhadoPorId(Long id) {
        Quarteirao quarteirao = buscarEntityById(id);
        
        List<LadoDetalhadoResponse> lados = ladoService.listarDetalhadoPorQuarteirao(id);

        return QuarteiraoDetalhadoResponse.fromEntity(quarteirao, lados);
    }

    
    public QuarteiraoResponse atualizar(Long id, QuarteiraoRequest request) {
        Quarteirao quarteirao = buscarEntityById(id);

        Optional.ofNullable(request.numero()).ifPresent(quarteirao::setNumero);
        Optional.ofNullable(request.sequencia()).ifPresent(quarteirao::setSequencia);
        
        Optional.ofNullable(request.localidadeId())
            .map(localidade -> 
                localidadeService.buscarEntityPorId(request.localidadeId())
            )
            .ifPresent(quarteirao::setLocalidade);

        Optional.ofNullable(request.areaId())
            .map(area -> 
                areaService.buscarEntityPorId(request.areaId())
            )
            .ifPresent(quarteirao::setArea);

        return QuarteiraoResponse.fromEntity(quarteiraoRepository.save(quarteirao));
    }

    @Transactional
    public void deletar(Long id) {
        Quarteirao quarteirao = buscarEntityById(id);

        Integer numero = quarteirao.getNumero();
        Integer sequencia = quarteirao.getSequencia();
        Long idLocalidade = quarteirao.getLocalidade().getId();

        quarteiraoRepository.delete(quarteirao);
        quarteiraoRepository.reordenarSequenciaLocalidade(numero, idLocalidade, sequencia);
    }

    public Quarteirao buscarEntityById(Long id) {
        return quarteiraoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quarteirão não encontrado com id: " + id));
    }

}
