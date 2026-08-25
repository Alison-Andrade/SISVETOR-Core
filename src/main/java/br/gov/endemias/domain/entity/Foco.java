package br.gov.endemias.domain.entity;

import br.gov.endemias.domain.enums.TipoDeposito;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "focos")
public class Foco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_tubito")
    private String numeroTubito;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_deposito")
    private TipoDeposito tipoDeposito;

    @Column(name = "resultado_laboratorio")
    private String resultadoLaboratorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visita_id")
    private Visita visita;
}
