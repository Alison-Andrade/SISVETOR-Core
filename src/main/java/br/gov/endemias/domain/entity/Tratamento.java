package br.gov.endemias.domain.entity;

import java.math.BigDecimal;

import br.gov.endemias.domain.enums.StatusVisita;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tratamentos")
@PrimaryKeyJoinColumn(name = "visita_id")
@Getter
@Setter
public class Tratamento extends Visita {

    @Enumerated(EnumType.STRING)
    private StatusVisita status;
    
    @Column(name = "qntd_eliminados")
    private Integer numEliminados = 0;

    @Column(name = "qntd_tratados")
    private Integer numTratados = 0;

    @Column(name = "qntd_larvicida")
    private BigDecimal qntdLarvicida = BigDecimal.ZERO;

    @Column(name = "tipo_larvicida")
    private String tipoLarvicida = "";
}
