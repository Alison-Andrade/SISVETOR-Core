package br.gov.endemias.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.Ciclo;
import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.domain.entity.Tratamento;
import br.gov.endemias.config.security.JWTUserData;
import br.gov.endemias.domain.enums.StatusVisita;
import br.gov.endemias.dto.TratamentoRequest;
import br.gov.endemias.dto.TratamentoResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.TratamentoRepository;
import br.gov.endemias.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TratamentoService {
    
    private final TratamentoRepository tratamentoRepository;
    private final ImovelService imovelService;
    private final CicloService cicloService;
    private final AgenteService agenteService;
    private final UserRepository userRepository;

    public TratamentoResponse cadastrar(TratamentoRequest request, Authentication autenticacao) {
        if (autenticacao.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CAMPO"))) {
            if (!(autenticacao.getPrincipal() instanceof JWTUserData usuario)) {
                throw new AccessDeniedException("Identidade do agente não disponível.");
            }
            boolean agenteDoUsuario = userRepository.findByIdWithAgente(usuario.userId())
                .map(user -> user.getAgente().getId().equals(request.agenteId()))
                .orElse(false);
            if (!agenteDoUsuario) {
                throw new AccessDeniedException("O agente só pode registrar o próprio tratamento.");
            }
        }
        
        if (request.status() == StatusVisita.TRABALHADO && (tratamentoRepository.existsByCicloIdAndImovelIdAndStatus(request.cicloId(), request.imovelId(), StatusVisita.TRABALHADO) || tratamentoRepository.existsByCicloIdAndImovelIdAndStatus(request.cicloId(), request.imovelId(), StatusVisita.RECUPERADO))) {
            throw new RegraNegocioException("Imovel já trabalhado neste ciclo.");
        }

        Tratamento tratamento = request.toEntity();
        
        if (tratamento.getStatus() == StatusVisita.TRABALHADO && tratamentoRepository.existsByCicloIdAndImovelIdAndStatus(request.cicloId(), request.imovelId(), StatusVisita.FECHADO)) {
            tratamento.setStatus(StatusVisita.RECUPERADO);
        }

        Imovel imovel = imovelService.buscarEntityPorId(request.imovelId());
        tratamento.setImovel(imovel);

        Ciclo ciclo = cicloService.buscarEntityPorId(request.cicloId());
        tratamento.setCiclo(ciclo);

        Agente agente = agenteService.buscarEntityPorId(request.agenteId());
        tratamento.setAgente(agente);
        
        Tratamento tratamentoSalvo = tratamentoRepository.save(tratamento);
        
        return TratamentoResponse.fromEntity(tratamentoSalvo);
    }

    public TratamentoResponse buscarPorId(Long id) {
        return TratamentoResponse.fromEntity(buscarEntityPorId(id));
    }

    public PagedModel<TratamentoResponse> listar(Pageable pageable) {
        Page<Tratamento> tratamentos = tratamentoRepository.findAll(pageable);
        return new PagedModel<>(tratamentos.map(TratamentoResponse::fromEntity));
    }

    public Tratamento buscarEntityPorId(Long id) {
        return tratamentoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tratamento nao encontrado com id: " + id));
    }

}
