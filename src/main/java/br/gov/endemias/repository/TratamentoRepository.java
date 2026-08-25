package br.gov.endemias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.Tratamento;
import br.gov.endemias.domain.enums.StatusVisita;

public interface TratamentoRepository extends JpaRepository<Tratamento, Long> {

    boolean existsByCicloIdAndImovelIdAndStatus(Long cicloId, Long imovelId, StatusVisita status);

}
