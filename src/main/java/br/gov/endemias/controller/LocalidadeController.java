package br.gov.endemias.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.LocalidadeRequest;
import br.gov.endemias.dto.LocalidadeResponse;
import br.gov.endemias.service.LocalidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/localidades")
@RequiredArgsConstructor
public class LocalidadeController {
    
    private final LocalidadeService localidadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalidadeResponse cadastrar(@RequestBody @Valid LocalidadeRequest request) {
        return localidadeService.cadastrar(request);
    }

    @GetMapping
    public PagedModel<LocalidadeResponse> listar(Pageable pageable) {
        return localidadeService.listar(pageable);
    }

    @GetMapping("/{id}")
    public LocalidadeResponse buscarPorId(@PathVariable Long id) {
        return localidadeService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LocalidadeResponse atualizar(@PathVariable Long id, @RequestBody @Valid LocalidadeRequest request) {
        return localidadeService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        localidadeService.deletar(id);
    }
}
