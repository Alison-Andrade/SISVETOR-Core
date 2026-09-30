package br.gov.endemias.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.UserRole;
import br.gov.endemias.domain.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query(
        """
           SELECT u FROM User u JOIN FETCH u.agente a
           WHERE a.cpf = :loginInput
           OR a.email = :loginInput
           OR a.matricula = :loginInput
        """
    )
    Optional<User> findByLoginInput(String loginInput);

    @Query("SELECT u FROM User u JOIN FETCH u.agente WHERE u.id = :id")
    Optional<User> findByIdWithAgente(@Param("id") Long id);

    boolean existsByAgenteId(Long agenteId);

    boolean existsByRole(UserRole role);

    @EntityGraph(attributePaths = "agente")
    Page<User> findAllByStatus(UserStatus status, Pageable pageable);

}
