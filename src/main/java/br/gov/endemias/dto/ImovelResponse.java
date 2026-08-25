package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.domain.enums.TipoImovel;

public record ImovelResponse(
    Long id,
    String placa,
    Integer sequencia,
    Integer numeroSms,
    Integer numeroResidentes,
    Integer numeroCaes,
    Integer numeroGatos,
    TipoImovel tipo,
    Long ladoId,
    Long localidadeId
) {
    public static ImovelResponse fromEntity(Imovel imovel) {
        return new ImovelResponse(
            imovel.getId(),
            imovel.getPlaca(),
            imovel.getSequencia(),
            imovel.getNumeroSms(),
            imovel.getNumeroResidentes(),
            imovel.getNumeroCaes(),
            imovel.getNumeroGatos(),
            imovel.getTipo(),
            imovel.getLado().getId(),
            imovel.getLocalidade() != null ? imovel.getLocalidade().getId() : null
        );
    }
}
