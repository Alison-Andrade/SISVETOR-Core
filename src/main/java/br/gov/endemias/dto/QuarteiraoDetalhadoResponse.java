package br.gov.endemias.dto;

import java.util.List;

import br.gov.endemias.domain.entity.Quarteirao;

public record QuarteiraoDetalhadoResponse(
    Long id, 
    Integer numero, 
    Integer sequencia, 
    List<LadoDetalhadoResponse> lados
) {
    public static QuarteiraoDetalhadoResponse fromEntity(Quarteirao quarteirao, List<LadoDetalhadoResponse> lados) {
        return new QuarteiraoDetalhadoResponse(
            quarteirao.getId(),
            quarteirao.getNumero(),
            quarteirao.getSequencia(),
            lados
        );
    }
}
