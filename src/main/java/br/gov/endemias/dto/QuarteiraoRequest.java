package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Quarteirao;
import jakarta.validation.constraints.NotNull;

public record QuarteiraoRequest(
    @NotNull(message = "O numero do quarteirão é obrigatório.")
    Integer numero,
    Integer sequencia,
    @NotNull(message = "A localidade do quarteirão é obrigatória.")
    Long localidadeId,
    Long areaId
) {
    public Quarteirao toEntity() {
        Quarteirao quarteirao = new Quarteirao();
        preecher(quarteirao);
        return quarteirao;
    }

    public void preecher(Quarteirao quarteirao) {
        quarteirao.setNumero(this.numero);
        quarteirao.setSequencia(this.sequencia != null ? this.sequencia : 0);
    }
}
