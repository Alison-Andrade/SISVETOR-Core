package br.gov.endemias.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Ciclo;
import br.gov.endemias.dto.CicloRequest;
import br.gov.endemias.dto.CicloResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.CicloRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CicloService {
    
    private final CicloRepository cicloRepository;

    public CicloResponse cadastrar(CicloRequest request) {
        Boolean cicloAberto = cicloRepository.findFirstByConcluidoFalse().isPresent();
        if (cicloAberto) {
            throw new RegraNegocioException("Já existe um ciclo aberto. Conclua o ciclo atual antes de criar um novo.");
        }

        Ciclo ciclo = request.toEntity();
        Ciclo cicloSalvo = cicloRepository.save(ciclo);
        return CicloResponse.fromEntity(cicloSalvo);
    }

    public CicloResponse buscarCicloPorId(Long id) {
        Ciclo ciclo = buscarEntityPorId(id);
        return CicloResponse.fromEntity(ciclo);
    }

    public CicloResponse buscarCicloAberto() {
        Ciclo cicloAberto = cicloRepository.findFirstByConcluidoFalse()
            .orElseThrow(() -> new ResourceNotFoundException("Nenhum ciclo aberto encontrado"));
        return CicloResponse.fromEntity(cicloAberto);
    }

    public PagedModel<CicloResponse> listarCiclos(Pageable pageable) {
        Page<Ciclo> ciclos = cicloRepository.findAll(pageable);
        return new PagedModel<>(ciclos.map(CicloResponse::fromEntity));
    }

    public CicloResponse concluirCiclo() {
        Ciclo cicloAberto = cicloRepository.findFirstByConcluidoFalse()
            .orElseThrow(() -> new ResourceNotFoundException("Nenhum ciclo aberto encontrado"));
        cicloAberto.setConcluido(true);
        Ciclo cicloAtualizado = cicloRepository.save(cicloAberto);
        return CicloResponse.fromEntity(cicloAtualizado);
    }

    public CicloResponse reabrirCiclo(Long id) {

        Boolean cicloAberto = cicloRepository.findFirstByConcluidoFalse().isPresent();
        if (cicloAberto) {
            throw new RegraNegocioException("Já existe um ciclo aberto. Conclua o ciclo atual antes de criar um novo.");
        }

        Ciclo ciclo = buscarEntityPorId(id);
        ciclo.setConcluido(false);
        Ciclo cicloAtualizado = cicloRepository.save(ciclo);
        return CicloResponse.fromEntity(cicloAtualizado);
    }

    public void deletarCiclo(Long id) {
        Ciclo ciclo = buscarEntityPorId(id);
        cicloRepository.delete(ciclo);
    }

    public Ciclo buscarEntityPorId(Long id) {
        return cicloRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ciclo não encontrado com id: " + id));
    }

}
