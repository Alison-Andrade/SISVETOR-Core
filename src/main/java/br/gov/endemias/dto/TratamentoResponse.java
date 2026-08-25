package br.gov.endemias.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.gov.endemias.domain.entity.Tratamento;
import br.gov.endemias.domain.enums.StatusVisita;

public record TratamentoResponse(
    Long id,
    LocalDateTime dataVisita,
    String observacao,
    ImovelResponse imovel,
    AgenteResponse agente,
    CicloResponse ciclo,
    StatusVisita status,
    Integer numEliminados,
    String tipoLarvicida,
    BigDecimal qntdLarvicida,
    Integer numTratados
) {
    public static TratamentoResponse fromEntity(Tratamento tratamento) {
        return new TratamentoResponse(
            tratamento.getId(),
            tratamento.getDataVisita(),
            tratamento.getObservacao(),
            ImovelResponse.fromEntity(tratamento.getImovel()),
            AgenteResponse.fromEntity(tratamento.getAgente()),
            CicloResponse.fromEntity(tratamento.getCiclo()),
            tratamento.getStatus(),
            tratamento.getNumEliminados(),
            tratamento.getTipoLarvicida(),
            tratamento.getQntdLarvicida(),
            tratamento.getNumTratados()
        );
    }
}
