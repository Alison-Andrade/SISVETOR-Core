package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.enums.FuncaoAgente;

public record AgenteResponse(
    Long id,
    String cpf,
    String matricula,
    String nome,
    String email,
    String telefone,
    FuncaoAgente funcaoAgente,
    Long supervisorId
) {
    public static AgenteResponse fromEntity(Agente agente) {
        return new AgenteResponse(
            agente.getId(),
            agente.getCpf(),
            agente.getMatricula(),
            agente.getNome(),
            agente.getEmail(),
            agente.getTelefone(),
            agente.getFuncao(),
            agente.getSupervisor() != null ? agente.getSupervisor().getId() : null
        );
    }
}
