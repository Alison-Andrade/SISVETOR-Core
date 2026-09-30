package br.gov.endemias.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.gov.endemias.config.security.JWTUserData;
import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.StatusVisita;
import br.gov.endemias.dto.TratamentoRequest;
import br.gov.endemias.repository.TratamentoRepository;
import br.gov.endemias.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TratamentoServiceSecurityTest {

    @Mock TratamentoRepository tratamentoRepository;
    @Mock ImovelService imovelService;
    @Mock CicloService cicloService;
    @Mock AgenteService agenteService;
    @Mock UserRepository userRepository;
    @InjectMocks TratamentoService tratamentoService;

    @Test
    void agenteDeCampoNaoRegistraTratamentoParaOutroAgente() {
        Agente agente = new Agente();
        agente.setId(10L);
        User user = new User();
        user.setAgente(agente);
        when(userRepository.findByIdWithAgente(1L)).thenReturn(Optional.of(user));
        var autenticacao = new UsernamePasswordAuthenticationToken(
            new JWTUserData(1L, "12345678909", "ROLE_CAMPO"), null,
            List.of(new SimpleGrantedAuthority("ROLE_CAMPO")));
        var request = new TratamentoRequest(null, 20L, 99L, 30L,
            StatusVisita.TRABALHADO, null, null, null, null);

        assertThrows(AccessDeniedException.class,
            () -> tratamentoService.cadastrar(request, autenticacao));
    }
}
