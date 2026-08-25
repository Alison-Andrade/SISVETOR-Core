package br.gov.endemias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.gov.endemias.domain.entity.Quarteirao;
import br.gov.endemias.dto.QuarteiraoResponse;

public interface QuarteiraoRepository extends JpaRepository<Quarteirao, Long> {

    boolean existsByNumeroAndLocalidadeId(Integer numero, Long localidadeId);
    
    Optional<Quarteirao> findFirstByNumeroAndLocalidadeIdOrderBySequenciaDesc(Integer numero, Long localidadeId);

    List<QuarteiraoResponse> findAllByLocalidadeId(Long localidadeId);

    @Modifying
    @Query(
        "UPDATE Quarteirao q SET q.sequencia = q.sequencia - 1 " +
        "WHERE q.numero = :numero " + 
        "AND q.localidade.id = :localidadeId " +
        "AND q.sequencia > :sequenciaDeletada"
    )
    void reordenarSequenciaLocalidade(Integer numero, Long localidadeId, Integer sequenciaDeletada);
}
