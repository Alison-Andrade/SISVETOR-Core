package br.gov.endemias.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.dto.ImovelResponse;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {
    
    List<ImovelResponse> findAllByLadoId(Long ladoId);

    List<Imovel> findAllByLadoIdIn(List<Long> ladoIdList);

    Optional<Imovel> findFirstByLadoIdOrderByOrdemDesc(Long ladoId);

    Optional<Imovel> findFirstByLadoIdOrderByNumeroSmsDesc(Long ladoId);

    Optional<Imovel> findFirstByLocalidadeIdOrderByNumeroSmsDesc(Long localidadeId);

    Optional<Imovel> findFirstByPlacaAndLadoIdOrderBySequenciaDesc(String placa, Long ladoId);

    boolean existsByPlacaAndLadoId(String placa, Long ladoId);

    @Modifying
    @Query(
        "UPDATE Imovel i SET i.numeroSms = i.numeroSms + 1 " +  
        "WHERE i.lado.id = :ladoId AND i.numeroSms >= :novoNumero"
    )
    int abrirEspacoParaNovoImovel(Long ladoId, Integer novoNumero);

    @Modifying
    @Query(
        """
        UPDATE Imovel i SET i.ordem = i.ordem + 1
        WHERE i.lado.id = :ladoId AND i.ordem >= :ordem
        """
    )
    void reordenarImoveis(Integer ordem);
}
