package br.gov.endemias.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import br.gov.endemias.repository.AgenteRepository;
import br.gov.endemias.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock AgenteRepository agenteRepository;
    @Mock AgenteService agenteService;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void cadastroPublicoSempreCriaContaDeCampoPendente() {
        Agente agente = agente(1L, FuncaoAgente.CAMPO);
        AgenteCadastroRequest dados = new AgenteCadastroRequest(
            agente.getNome(), agente.getCpf(), agente.getMatricula(), null, agente.getEmail());
        when(agenteRepository.findByCpf(dados.cpf())).thenReturn(Optional.empty());
        when(agenteService.cadastrar(any(AgenteRequest.class)))
            .thenReturn(AgenteResponse.fromEntity(agente));
        when(agenteService.buscarEntityPorId(1L)).thenReturn(agente);
        when(passwordEncoder.encode("senha-segura")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.cadastrarPublico(new CadastroPublicoRequest(dados, "senha-segura"));

        assertEquals(UserRole.ROLE_CAMPO, response.role());
        assertEquals(UserStatus.PENDENTE, response.status());
        ArgumentCaptor<AgenteRequest> agenteRequest = ArgumentCaptor.forClass(AgenteRequest.class);
        verify(agenteService).cadastrar(agenteRequest.capture());
        assertEquals(FuncaoAgente.CAMPO, agenteRequest.getValue().funcao());
        ArgumentCaptor<User> usuario = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(usuario.capture());
        assertEquals("hash", usuario.getValue().getPassword());
    }

    @Test
    void coordenadorNaoPodeCriarOutroCoordenador() {
        var autenticacao = new UsernamePasswordAuthenticationToken("coordenador", null,
            List.of(new SimpleGrantedAuthority("ROLE_COORDENADOR")));
        var request = new CadastroAdministrativoRequest(1L, "senha-segura", UserRole.ROLE_COORDENADOR);

        assertThrows(AccessDeniedException.class,
            () -> userService.cadastrarPorCoordenador(request, autenticacao));
    }

    @Test
    void coordenadorCriaContaAtivaParaSupervisorCadastrado() {
        Agente supervisor = agente(2L, FuncaoAgente.SUPERVISOR);
        var autenticacao = new UsernamePasswordAuthenticationToken("coordenador", null,
            List.of(new SimpleGrantedAuthority("ROLE_COORDENADOR")));
        var request = new CadastroAdministrativoRequest(2L, "senha-segura", UserRole.ROLE_SUPERVISOR);
        when(agenteService.buscarEntityPorId(2L)).thenReturn(supervisor);
        when(passwordEncoder.encode("senha-segura")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.cadastrarPorCoordenador(request, autenticacao);

        assertEquals(UserRole.ROLE_SUPERVISOR, response.role());
        assertEquals(UserStatus.ATIVO, response.status());
    }

    @Test
    void aprovacaoAtivaSomenteContaPendenteDeCampo() {
        User user = new User();
        user.setId(5L);
        user.setAgente(agente(1L, FuncaoAgente.CAMPO));
        user.setRole(UserRole.ROLE_CAMPO);
        user.setStatus(UserStatus.PENDENTE);
        when(userRepository.findByIdWithAgente(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userService.aprovar(5L);

        assertEquals(UserStatus.ATIVO, response.status());
    }

    private Agente agente(Long id, FuncaoAgente funcao) {
        Agente agente = new Agente();
        agente.setId(id);
        agente.setNome("Agente de Teste");
        agente.setCpf("12345678909");
        agente.setMatricula("1234567");
        agente.setEmail("agente@example.com");
        agente.setFuncao(funcao);
        return agente;
    }
}
