package br.gov.endemias.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.gov.endemias.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query(
        """
           SELECT u FROM User u JOIN u.agente a
           WHERE a.cpf = :loginInput
           OR a.email = :loginInput
           OR a.matricula = :loginInput
        """
    )
    Optional<User> findByLoginInput(String loginInput);

}
