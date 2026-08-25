package br.gov.endemias.domain.entity;

import br.gov.endemias.domain.enums.TipoImovel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "imoveis", 
    indexes = {
        @Index(name="idx_imovel_lado", columnList = "lado_id")
    }
)
@Getter
@Setter
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placa;

    @Column(name = "numero_sms")
    private Integer numeroSms;

    private Integer sequencia;

    private Integer ordem;
    
    @Column(name = "num_residentes")
    private Integer numeroResidentes = 0;

    @Column(name = "num_caes")
    private Integer numeroCaes = 0;

    @Column(name = "num_gatos")
    private Integer numeroGatos = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoImovel tipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lado_id")
    private Lado lado;

    @ManyToOne
    @JoinColumn(name = "localidade_id")
    private Localidade localidade;

}