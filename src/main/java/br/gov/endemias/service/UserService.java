package br.gov.endemias.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.FuncaoAgente;
import br.gov.endemias.dto.AgenteRequest;
import br.gov.endemias.dto.AgenteResponse;
import br.gov.endemias.dto.UserRequest;
import br.gov.endemias.dto.UserResponse;
import br.gov.endemias.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AgenteService agenteService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse cadastrar(UserRequest request) {

        AgenteRequest agenteInicial = new AgenteRequest(
            request.agente().nome(),
            request.agente().cpf(),
            request.agente().matricula(),
            request.agente().telefone(),
            request.agente().email(),
            FuncaoAgente.CAMPO,
            null
        );

        AgenteResponse agenteResponse = agenteService.cadastrar(agenteInicial);

        Agente agenteSalvo = agenteService.buscarEntityPorId(agenteResponse.id());
        
        User user = request.toEntity();
        user.setAgente(agenteSalvo);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userSalvo = userRepository.save(user);

        return UserResponse.fromEntity(userSalvo);
    } 

}
