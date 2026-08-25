package br.gov.endemias.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "liraas")
@PrimaryKeyJoinColumn(name = "visita_id")
@Getter
@Setter
public class Liraa extends Visita {
    
}
