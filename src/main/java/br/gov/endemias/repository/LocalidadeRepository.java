package br.gov.endemias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.Localidade;


public interface LocalidadeRepository extends JpaRepository<Localidade, Long>{
    boolean existsByCodigo(String codigo);

    Optional<Localidade> findByCodigo(String codigo);
}
