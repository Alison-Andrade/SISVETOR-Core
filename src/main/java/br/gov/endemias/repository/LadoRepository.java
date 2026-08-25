package br.gov.endemias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.gov.endemias.domain.entity.Lado;
import br.gov.endemias.dto.LadoResponse;

public interface LadoRepository extends JpaRepository<Lado, Long> {
    boolean existsByQuarteiraoIdAndNumero(Long quarteiraoId, Integer numero);

    Optional<Lado> findFirstByQuarteiraoIdAndNumeroOrderByNumeroDesc(Long quarteiraoId, Integer numero);

    Optional<Lado> findFirstByQuarteiraoIdOrderByNumeroDesc(Long quarteiraoId);

    List<LadoResponse> findAllByQuarteiraoId(Long quarteiraoId);
    
    List<Lado> findAllDetalhadoByQuarteiraoId(Long quarteiraoId);

    @Modifying
    @Query(
        "UPDATE Lado l SET l.numero = l.numero + 1 " +  
        "WHERE l.quarteirao.id = :quarteiraoId AND l.numero >= :numero"
    )
    int abrirEspacoParaNovoLado(Long quarteiraoId, Integer numero);
}
