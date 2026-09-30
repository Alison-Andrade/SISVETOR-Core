package br.gov.endemias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.gov.endemias.domain.entity.Imovel;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {
    
    List<Imovel> findAllByLadoIdOrderByOrdemAsc(Long ladoId);

    List<Imovel> findAllByLadoIdIn(List<Long> ladoIdList);

    Optional<Imovel> findFirstByLadoIdOrderByOrdemDesc(Long ladoId);

    Optional<Imovel> findFirstByLocalidadeIdOrderByOrdemDesc(Long localidadeId);

    Optional<Imovel> findFirstByLadoIdOrderByNumeroSmsDesc(Long ladoId);

    Optional<Imovel> findFirstByLocalidadeIdOrderByNumeroSmsDesc(Long localidadeId);

    Optional<Imovel> findFirstByPlacaAndLadoIdOrderBySequenciaDesc(String placa, Long ladoId);

    Optional<Imovel> findFirstByPlacaAndLocalidadeIdOrderBySequenciaDesc(String placa, Long localidadeId);

    @Modifying
    @Query(
        "UPDATE Imovel i SET i.numeroSms = i.numeroSms + 1 " +  
        "WHERE i.lado.id = :ladoId AND i.numeroSms >= :novoNumero"
    )
    int abrirEspacoParaNovoImovel(Long ladoId, Integer novoNumero);

    @Modifying
    @Query(
        "UPDATE Imovel i SET i.numeroSms = i.numeroSms + 1 " +
        "WHERE i.localidade.id = :localidadeId AND i.numeroSms >= :novoNumero"
    )
    int abrirEspacoParaNovoImovelNaLocalidade(Long localidadeId, Integer novoNumero);

    @Modifying
    @Query(
        """
        UPDATE Imovel i SET i.ordem = i.ordem + 1
        WHERE i.lado.id = :ladoId AND i.ordem >= :ordem
        """
    )
    void reordenarImoveisNoLado(Long ladoId, Integer ordem);

    @Modifying
    @Query(
        """
        UPDATE Imovel i SET i.ordem = i.ordem + 1
        WHERE i.localidade.id = :localidadeId AND i.ordem >= :ordem
        """
    )
    void reordenarImoveisNaLocalidade(Long localidadeId, Integer ordem);
}
