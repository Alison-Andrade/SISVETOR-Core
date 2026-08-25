package br.gov.endemias.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "visitas", 
    indexes = {
        @Index(name="idx_visita_ciclo", columnList = "ciclo_id"),
        @Index(name="idx_visita_imovel", columnList = "imovel_id"),
        @Index(name="idx_visita_agente", columnList = "agente_id")
    }
)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_visita")
    private LocalDateTime dataVisita;

    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_id")
    private Ciclo ciclo;

    @PrePersist
    public void prePersist() {
        this.dataVisita = LocalDateTime.now();
    }

    
}
