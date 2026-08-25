package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Imovel;
import br.gov.endemias.domain.enums.TipoImovel;

public record ImovelRequest(
    String placa,
    Integer sequencia,
    Integer numeroSms,
    Integer ordem,
    Integer numeroResidentes,
    Integer numeroCaes,
    Integer numeroGatos,
    TipoImovel tipo,
    Long ladoId,
    Long localidadeId
) {
    public Imovel toEntity() {
        Imovel imovel = new Imovel();
        preencher(imovel);
        
        return imovel;
    }

    public void preencher(Imovel imovel) {
        imovel.setPlaca(this.placa);
        imovel.setSequencia(this.sequencia);
        imovel.setNumeroSms(this.numeroSms);
        imovel.setNumeroResidentes(this.numeroResidentes);
        imovel.setNumeroCaes(this.numeroCaes);
        imovel.setNumeroGatos(this.numeroGatos);
        imovel.setTipo(this.tipo);
    }
}
