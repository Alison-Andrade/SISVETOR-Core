package br.gov.endemias.dto;

import java.math.BigDecimal;

import br.gov.endemias.domain.entity.Tratamento;
import br.gov.endemias.domain.enums.StatusVisita;
import jakarta.validation.constraints.NotNull;

public record TratamentoRequest(
    String observacao,

    @NotNull(message = "O imovel é obrigatório.")
    Long imovelId,
    
    @NotNull(message = "O agente é obrigatório.")
    Long agenteId,
    
    @NotNull(message = "O ciclo é obrigatório.")
    Long cicloId,

    @NotNull(message = "O status da visita é obrigatório.")
    StatusVisita status,

    Integer numEliminados,
    String tipoLarvicida,
    BigDecimal qntdLarvicida,
    Integer numTratados
) {
    public Tratamento toEntity() {
        Tratamento tratamento = new Tratamento();
        preencher(tratamento);
        return tratamento;
    }

    private void preencher(Tratamento tratamento) {
        tratamento.setObservacao(observacao);
        tratamento.setStatus(status);
        tratamento.setNumEliminados(numEliminados != null ? numEliminados : 0);
        tratamento.setNumTratados(numTratados != null ? numTratados : 0);
        tratamento.setQntdLarvicida(qntdLarvicida != null ? qntdLarvicida : BigDecimal.ZERO);
        tratamento.setTipoLarvicida(tipoLarvicida);
    }
}
