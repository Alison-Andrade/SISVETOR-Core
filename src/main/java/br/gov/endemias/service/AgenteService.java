package br.gov.endemias.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.enums.FuncaoAgente;
import br.gov.endemias.dto.AgenteRequest;
import br.gov.endemias.dto.AgenteResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.AgenteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgenteService {
    
    private final AgenteRepository agenteRepository;


    public AgenteResponse cadastrar(AgenteRequest request) {
        if (request.cpf() != null && agenteRepository.existsByCpf(request.cpf())) {
            throw new RegraNegocioException("Já existe agente cadastrado com esse cpf.");
        }

        if (request.email() != null && agenteRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe agente cadastrado com esse e-mail.");
        }

        Agente supervisor = buscarEValidarSupervisor(request.supervisorId());
        Agente agente = request.toEntity();
        agente.setSupervisor(supervisor);

        Agente agenteSalvo = agenteRepository.save(agente);

        return AgenteResponse.fromEntity(agenteSalvo);
    }

    public PagedModel<AgenteResponse> listar(Pageable pageable) {
        Page<Agente> agentes = agenteRepository.findAll(pageable);

        return new PagedModel<>(agentes.map(AgenteResponse::fromEntity));
    }

    public AgenteResponse buscarPorId(Long id) {
        Agente agente = buscarEntityPorId(id);
        return AgenteResponse.fromEntity(agente);   
    }

    public AgenteResponse atualizar(Long id, AgenteRequest request) {
        Agente agente = buscarEntityPorId(id);

        if (request.cpf() != null && !request.cpf().equals(agente.getCpf()) && agenteRepository.existsByCpf(request.cpf())) {
            throw new RegraNegocioException("Já existe outro agente cadastrado com esse cpf.");
        }

        FuncaoAgente funcaoAtual = agente.getFuncao();

        request.preencher(agente);

        if (request.funcao() == null) {
            agente.setFuncao(funcaoAtual);
        }

        Agente supervisor = buscarEValidarSupervisor(request.supervisorId());
        agente.setSupervisor(supervisor);
        
        Agente agenteAtualizado = agenteRepository.save(agente);
        return AgenteResponse.fromEntity(agenteAtualizado);
    }

    public void delete(Long id) {
        Agente agente = buscarEntityPorId(id);
        agenteRepository.delete(agente);
    }

    public Agente buscarEntityPorId(Long id) {
        return agenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agente não encontrado"));
    }

    private Agente buscarEValidarSupervisor(Long supervisorId) {

        if (supervisorId == null) {
            return null;
        }

        Agente supervisor = agenteRepository.findById(supervisorId)
        .orElseThrow(() -> new ResourceNotFoundException("Supervisor não encontrado."));

        if (supervisor.getFuncao() != FuncaoAgente.SUPERVISOR) {
            throw new RegraNegocioException("O agente informado não é SUPERVISOR.");
        }

        return supervisor;
    }
}
