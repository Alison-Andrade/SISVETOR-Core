package br.gov.endemias.dto;

import org.hibernate.validator.constraints.br.CPF;

import br.gov.endemias.domain.enums.FuncaoAgente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgenteCadastroRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Size(min = 11, max = 11) @CPF String cpf,
    @NotBlank @Size(min = 7, max = 7) String matricula,
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9\\d{4}-?\\d{4}$") String telefone,
    @NotBlank @Email @Size(max = 150) String email
) {
    public AgenteRequest toAgenteRequest() {
        return new AgenteRequest(nome, cpf, matricula, telefone, email, FuncaoAgente.CAMPO, null);
    }
}
