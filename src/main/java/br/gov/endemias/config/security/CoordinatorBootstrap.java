package br.gov.endemias.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.gov.endemias.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(name = "bootstrap.coordinator.enabled", havingValue = "true")
@RequiredArgsConstructor
public class CoordinatorBootstrap implements ApplicationRunner {

    private final UserService userService;

    @Value("${BOOTSTRAP_COORDINATOR_NOME:}")
    private String nome;

    @Value("${BOOTSTRAP_COORDINATOR_CPF:}")
    private String cpf;

    @Value("${BOOTSTRAP_COORDINATOR_MATRICULA:}")
    private String matricula;

    @Value("${BOOTSTRAP_COORDINATOR_EMAIL:}")
    private String email;

    @Value("${BOOTSTRAP_COORDINATOR_PASSWORD:}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (nome.isBlank() || cpf.isBlank() || matricula.isBlank() || email.isBlank()
                || password.length() < 12) {
            throw new IllegalStateException("Configure os dados do coordenador inicial e uma senha de pelo menos 12 caracteres.");
        }
        userService.bootstrapCoordinator(nome, cpf, matricula, email, password);
    }
}
