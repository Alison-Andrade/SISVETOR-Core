package br.gov.endemias.domain.entity;

import br.gov.endemias.domain.enums.CategoriaLocalidade;
import br.gov.endemias.domain.enums.TipoLocalidade;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "localidades")
@Getter
@Setter
public class Localidade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nome;

    @Enumerated(EnumType.STRING)
    private CategoriaLocalidade categoria;

    @Enumerated(EnumType.STRING)
    private TipoLocalidade tipo;

}
