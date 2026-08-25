package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Lado;
import jakarta.validation.constraints.NotNull;

public record LadoRequest(
    Integer numero,
    String logradouro,

    @NotNull(message = "O quarteirão é obrigatório.")
    Long quarteiraoId
) {
    public Lado toEntity() {
        Lado lado = new Lado();
        lado.setLogradouro(this.logradouro);
        lado.setNumero(this.numero);
        return lado;
    }
}
