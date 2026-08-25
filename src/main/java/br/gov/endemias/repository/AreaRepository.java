package br.gov.endemias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
    
}
