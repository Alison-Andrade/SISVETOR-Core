package br.gov.endemias.dto;

public record CicloResponse(
    Long id,
    Integer numeroCiclo,
    Integer ano,
    String dataInicio,
    String dataFim,
    Boolean concluido
) {
    public static CicloResponse fromEntity(br.gov.endemias.domain.entity.Ciclo ciclo) {
        return new CicloResponse(
            ciclo.getId(),
            ciclo.getNumeroCiclo(),
            ciclo.getAno(),
            ciclo.getDataInicio() != null ? ciclo.getDataInicio().toString() : null,
            ciclo.getDataFim() != null ? ciclo.getDataFim().toString() : null,
            ciclo.getConcluido()
        );
    }
}
