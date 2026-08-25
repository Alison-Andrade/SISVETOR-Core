package br.gov.endemias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.Agente;

public interface AgenteRepository extends JpaRepository<Agente, Long>{
    
    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);


}
