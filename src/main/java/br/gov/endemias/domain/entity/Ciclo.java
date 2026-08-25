package br.gov.endemias.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "ciclos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_numero_ano_ciclo",
            columnNames = {"numero_ciclo", "ano"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Ciclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ciclo")
    private Integer numeroCiclo;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "concluido")
    private Boolean concluido = false;

    public void concluir() {
        this.concluido = true;
        this.dataFim = LocalDate.now();
    }

    public void reabrir() {
        this.concluido = false;
        this.dataFim = null;
    }
}
