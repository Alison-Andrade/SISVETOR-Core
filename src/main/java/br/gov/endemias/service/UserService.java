package br.gov.endemias.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.FuncaoAgente;
import br.gov.endemias.domain.enums.UserRole;
import br.gov.endemias.domain.enums.UserStatus;
import br.gov.endemias.dto.AgenteCadastroRequest;
import br.gov.endemias.dto.AgenteRequest;
import br.gov.endemias.dto.AgenteResponse;
import br.gov.endemias.dto.CadastroAdministrativoRequest;
import br.gov.endemias.dto.CadastroPublicoRequest;
import br.gov.endemias.dto.UserResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.AgenteRepository;
import br.gov.endemias.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AgenteRepository agenteRepository;
    private final AgenteService agenteService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse cadastrarPublico(CadastroPublicoRequest request) {
        AgenteCadastroRequest dados = request.agente();
        Agente agente = agenteRepository.findByCpf(dados.cpf())
            .map(existente -> validarAgenteExistente(existente, dados))
            .orElseGet(() -> criarAgente(dados.toAgenteRequest()));

        if (userRepository.existsByAgenteId(agente.getId())) {
            throw new RegraNegocioException("O agente já possui uma conta.");
        }

        return salvarUsuario(agente, request.password(), UserRole.ROLE_CAMPO, UserStatus.PENDENTE);
    }

    @Transactional
    public UserResponse cadastrarPorCoordenador(CadastroAdministrativoRequest request, Authentication autenticacao) {
        UserRole role = request.role();
        if (role == UserRole.ROLE_ADMIN ||
            (role == UserRole.ROLE_COORDENADOR && !temPapel(autenticacao, UserRole.ROLE_ADMIN))) {
            throw new AccessDeniedException("Não é permitido conceder esse papel.");
        }

        Agente agente = agenteService.buscarEntityPorId(request.agenteId());
        if (userRepository.existsByAgenteId(agente.getId())) {
            throw new RegraNegocioException("O agente já possui uma conta.");
        }
        if (roleParaFuncao(agente.getFuncao()) != role) {
            throw new RegraNegocioException("O papel da conta deve corresponder à função do agente.");
        }

        return salvarUsuario(agente, request.password(), role, UserStatus.ATIVO);
    }

    @Transactional
    public UserResponse aprovar(Long id) {
        User user = userRepository.findByIdWithAgente(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        if (user.getStatus() != UserStatus.PENDENTE || user.getRole() != UserRole.ROLE_CAMPO) {
            throw new RegraNegocioException("Somente cadastros de campo pendentes podem ser aprovados.");
        }
        user.setStatus(UserStatus.ATIVO);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public PagedModel<UserResponse> listarPendentes(Pageable pageable) {
        return new PagedModel<>(userRepository.findAllByStatus(UserStatus.PENDENTE, pageable)
            .map(UserResponse::fromEntity));
    }

    @Transactional
    public UserResponse bloquear(Long id, Authentication autenticacao) {
        User user = userRepository.findByIdWithAgente(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        if ((user.getRole() == UserRole.ROLE_COORDENADOR || user.getRole() == UserRole.ROLE_ADMIN)
                && !temPapel(autenticacao, UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("Não é permitido bloquear esse usuário.");
        }
        if (user.getStatus() == UserStatus.BLOQUEADO) {
            throw new RegraNegocioException("O usuário já está bloqueado.");
        }
        user.setStatus(UserStatus.BLOQUEADO);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserResponse desbloquear(Long id, Authentication autenticacao) {
        User user = userRepository.findByIdWithAgente(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        if ((user.getRole() == UserRole.ROLE_COORDENADOR || user.getRole() == UserRole.ROLE_ADMIN)
                && !temPapel(autenticacao, UserRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("Não é permitido desbloquear esse usuário.");
        }
        if (user.getStatus() != UserStatus.BLOQUEADO) {
            throw new RegraNegocioException("O usuário não está bloqueado.");
        }
        user.setStatus(UserStatus.ATIVO);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void bootstrapCoordinator(String nome, String cpf, String matricula, String email, String password) {
        if (userRepository.existsByRole(UserRole.ROLE_COORDENADOR)
                || userRepository.existsByRole(UserRole.ROLE_ADMIN)) {
            throw new IllegalStateException("Já existe um coordenador ou administrador. Desative o bootstrap.");
        }
        AgenteRequest agenteRequest = new AgenteRequest(nome, cpf, matricula, null, email,
            FuncaoAgente.COORDENADOR, null);
        Agente agente = criarAgente(agenteRequest);
        salvarUsuario(agente, password, UserRole.ROLE_COORDENADOR, UserStatus.ATIVO);
    }

    private Agente validarAgenteExistente(Agente agente, AgenteCadastroRequest dados) {
        if (agente.getFuncao() != FuncaoAgente.CAMPO ||
            !agente.getMatricula().trim().equals(dados.matricula()) ||
            !agente.getEmail().equalsIgnoreCase(dados.email())) {
            throw new RegraNegocioException("Os dados do agente não conferem. Procure a coordenação.");
        }
        return agente;
    }

    private Agente criarAgente(AgenteRequest request) {
        AgenteResponse response = agenteService.cadastrar(request);
        return agenteService.buscarEntityPorId(response.id());
    }

    private UserResponse salvarUsuario(Agente agente, String password, UserRole role, UserStatus status) {
        User user = new User();
        user.setAgente(agente);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(status);
        return UserResponse.fromEntity(userRepository.save(user));
    }

    private UserRole roleParaFuncao(FuncaoAgente funcao) {
        return switch (funcao) {
            case CAMPO -> UserRole.ROLE_CAMPO;
            case SUPERVISOR -> UserRole.ROLE_SUPERVISOR;
            case COORDENADOR -> UserRole.ROLE_COORDENADOR;
        };
    }

    private boolean temPapel(Authentication autenticacao, UserRole role) {
        return autenticacao.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }
}
