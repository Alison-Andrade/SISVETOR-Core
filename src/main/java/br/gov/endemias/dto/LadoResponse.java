package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Lado;

public record LadoResponse(
    Long id,
    Integer numero,
    String logradouro,
    Long quarteiraoId
) {
    public static LadoResponse fromEntity(Lado lado) {
        return new LadoResponse(
            lado.getId(),
            lado.getNumero(),
            lado.getLogradouro(),
            lado.getQuarteirao().getId()
        );
    }
}
