package br.gov.endemias.dto;

import org.hibernate.validator.constraints.br.CPF;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.enums.FuncaoAgente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgenteRequest(
    @NotBlank(message = "O nome do agente é obrigatório")
    @Size(max = 150, message = "O nome do agente deve ter no máximo 150 caracteres")
    String nome,

    @NotBlank(message = "O CPF do agente é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF do agente deve conter exatamente 11 caracteres")
    @CPF(message = "CPF inválido.")
    String cpf,

    @NotBlank(message = "A matricula do agente é obrigatória")
    @Size(min = 7, max = 7, message = "A matricula do agente deve conter exatamente 7 caracteres")
    String matricula,

    @Size(max = 20, message = "O telefone do agente deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9\\d{4}-?\\d{4}$", 
             message = "Formato de celular inválido (Ex: (00) 90000-0000)")
    String telefone,

    @NotBlank(message = "O email do agente é obrigatório")
    @Email(message = "E-mail inválido.")
    @Size(max = 150, message = "O email do agente deve ter no máximo 150 caracteres")
    String email,

    FuncaoAgente funcao,
    Long supervisorId
) {
    
    public Agente toEntity() {
        Agente agente = new Agente();
        preencher(agente);
        return agente;
    }

    public void preencher(Agente agente) {
        agente.setNome(this.nome);
        agente.setCpf(this.cpf);
        agente.setMatricula(this.matricula);
        agente.setTelefone(this.telefone);
        agente.setEmail(this.email);
        agente.setFuncao(this.funcao != null ? this.funcao : FuncaoAgente.CAMPO);
    }
}
