package br.gov.endemias.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Localidade;
import br.gov.endemias.dto.LocalidadeRequest;
import br.gov.endemias.dto.LocalidadeResponse;
import br.gov.endemias.exception.RegraNegocioException;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.LocalidadeRepository;

@Service
public class LocalidadeService {

    private final LocalidadeRepository localidadeRepository;

    public LocalidadeService(LocalidadeRepository localidadeRepository) {
        this.localidadeRepository = localidadeRepository;
    }

    public LocalidadeResponse cadastrar(LocalidadeRequest request) {
        
        if (request.codigo() != null && localidadeRepository.existsByCodigo(request.codigo())) {
            throw new RegraNegocioException("Já existe localidade com esse codigo.");
        }

        Localidade localidade = request.toEntity();
        Localidade localidadeSalva = localidadeRepository.save(localidade);
        return LocalidadeResponse.fromEntity(localidadeSalva);
    }

    public PagedModel<LocalidadeResponse> listar(Pageable pageable) {
        Page<Localidade> localidades = localidadeRepository.findAll(pageable);

        return new PagedModel<>(localidades.map(LocalidadeResponse::fromEntity));
    }

    public LocalidadeResponse buscarPorId(Long id) {
        Localidade localidade = buscarEntityPorId(id);
        return LocalidadeResponse.fromEntity(localidade);
    }

    public LocalidadeResponse atualizar(Long id, LocalidadeRequest request) {
        Localidade localidade = buscarEntityPorId(id);

        request.preencher(localidade);
        Localidade localidadeAtualizada = localidadeRepository.save(localidade);
        return LocalidadeResponse.fromEntity(localidadeAtualizada);
    }

    public void deletar(Long id) {
        Localidade localidade = buscarEntityPorId(id);
        localidadeRepository.delete(localidade);
    }

    public Localidade buscarEntityPorId(Long id) {
        return localidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade não encontrada com id: " + id));
    }

}
