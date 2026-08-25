package br.gov.endemias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.Ciclo;

public interface CicloRepository extends JpaRepository<Ciclo, Long> {

    public Optional<Ciclo> findFirstByConcluidoFalse();
    
}
