package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Localidade;
import br.gov.endemias.domain.enums.CategoriaLocalidade;
import br.gov.endemias.domain.enums.TipoLocalidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocalidadeRequest(
    @NotBlank(message = "O codigo da localidade é obrigatório.")
    String codigo,
    @NotBlank(message = "O nome da localidade é obrigatório.")
    String nome,
    @NotNull(message = "A categoria da localidade é obrigatória.")
    CategoriaLocalidade categoria,
    @NotNull(message = "O tipo da localidade é obrigatório.")
    TipoLocalidade tipo
) {
    public Localidade toEntity() {
        Localidade localidade = new Localidade();
        preencher(localidade);
        return localidade;
    }

    public void preencher(Localidade localidade) {
        localidade.setCodigo(this.codigo);
        localidade.setNome(this.nome);
        localidade.setCategoria(this.categoria);
        localidade.setTipo(this.tipo);
    }
}