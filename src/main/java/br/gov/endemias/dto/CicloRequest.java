package br.gov.endemias.dto;

import java.time.LocalDate;

import br.gov.endemias.domain.entity.Ciclo;

public record CicloRequest(
    Integer numeroCiclo,
    LocalDate dataInicio
) {
    public Ciclo toEntity() {
        Ciclo ciclo = new Ciclo();
        preencher(ciclo);
        return ciclo;
    }

    public void preencher(Ciclo ciclo) {
        ciclo.setNumeroCiclo(this.numeroCiclo);
        ciclo.setAno(LocalDate.now().getYear());
        ciclo.setDataInicio(this.dataInicio != null ? this.dataInicio : LocalDate.now());
    }
    
}
