package br.gov.endemias.dto;

import java.util.List;

import br.gov.endemias.domain.entity.Lado;

public record LadoDetalhadoResponse(
    Long id, 
    Integer numero, 
    String logradouro, 
    List<ImovelResponse> imoveis
) {
    public static LadoDetalhadoResponse fromEntity(Lado lado, List<ImovelResponse> imoveis) {
        return new LadoDetalhadoResponse(
            lado.getId(),
            lado.getNumero(),
            lado.getLogradouro(),
            imoveis
        );
    }
}
