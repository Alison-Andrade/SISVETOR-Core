package br.gov.endemias.service;

import br.gov.endemias.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.domain.entity.Lado;
import br.gov.endemias.dto.ImovelResponse;
import br.gov.endemias.dto.LadoDetalhadoResponse;
import br.gov.endemias.dto.LadoRequest;
import br.gov.endemias.dto.LadoResponse;
import br.gov.endemias.repository.LadoRepository;
import br.gov.endemias.repository.QuarteiraoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LadoService {
    
    private final LadoRepository ladoRepository;
    private final QuarteiraoRepository quarteiraoRepository;
    private final ImovelService imovelService;

    @Transactional
    public LadoResponse cadastrar(LadoRequest request) {

        Lado lado = request.toEntity();

        lado.setQuarteirao(quarteiraoRepository.findById(request.quarteiraoId())
                .orElseThrow(() -> new ResourceNotFoundException("Quarteirão nao encontrado"))
        );

        if (request.numero() != null) {
            ladoRepository.abrirEspacoParaNovoLado(request.quarteiraoId(), request.numero());
            lado.setNumero(request.numero());
        } else {
            Integer ultimoLado = ladoRepository.findFirstByQuarteiraoIdOrderByNumeroDesc(request.quarteiraoId()).map(ladoEntity -> ladoEntity.getNumero()).orElse(0);

            lado.setNumero(ultimoLado + 1);
        }

        if (request.logradouro() == null) {
            lado.setLogradouro("Projetada");
        }

        return LadoResponse.fromEntity(ladoRepository.save(lado));
    }

    public List<LadoResponse> listarPorQuarteirao(Long quarteiraoId) {
        return ladoRepository.findAllByQuarteiraoId(quarteiraoId);
    }

    public List<LadoDetalhadoResponse> listarDetalhadoPorQuarteirao(Long quarteiraoId) {
        List<Lado> lados = ladoRepository.findAllDetalhadoByQuarteiraoId(quarteiraoId);

        if (lados.isEmpty()) {
            return List.of();
        }

        List<Long> ladoIdList = lados
            .stream()
            .map(lado ->
                lado.getId()
            )
            .toList();

        List<Imovel> imoveis = imovelService.listarPorLadoIdIn(ladoIdList);

        Map<Long, List<ImovelResponse>> imoveisPorLado = imoveis
            .stream()
            .collect(Collectors.groupingBy(
                imovel -> imovel.getLado().getId(),
                Collectors.mapping(ImovelResponse::fromEntity, Collectors.toList())
            ));

        return lados
            .stream()
            .map(lado -> 
                LadoDetalhadoResponse.fromEntity(lado, imoveisPorLado.getOrDefault(lado.getId(), List.of()))
            )
            .toList();
    }

    public LadoResponse atualizar(Long id, LadoRequest request) {
        Lado lado = request.toEntity();
        lado.setId(id);

        // TO-DO

        return LadoResponse.fromEntity(ladoRepository.save(lado));
    }

    public void deletar(Long id) {
        ladoRepository.deleteById(id);
    }

    public Lado buscarEntityPorId(Long id) {
        return ladoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lado não encontrado com id: " + id));
    }

}

