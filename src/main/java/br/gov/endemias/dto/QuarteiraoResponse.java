package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Quarteirao;

public record QuarteiraoResponse(
    Long id,
    Integer numero,
    Integer sequencia,
    Long areaId
) {
    public static QuarteiraoResponse fromEntity(Quarteirao quarteirao) {
        return new QuarteiraoResponse(
            quarteirao.getId(),
            quarteirao.getNumero(),
            quarteirao.getSequencia(),
            quarteirao.getArea() != null ? quarteirao.getArea().getId() : null
        );
    }
}
