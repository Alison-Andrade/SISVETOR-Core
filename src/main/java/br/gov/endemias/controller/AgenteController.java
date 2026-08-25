package br.gov.endemias.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.AgenteRequest;
import br.gov.endemias.dto.AgenteResponse;
import br.gov.endemias.service.AgenteService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("api/v1/agentes")
public class AgenteController {
    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrar(@RequestBody @Valid AgenteRequest request) {
        return agenteService.cadastrar(request);
    }

    @GetMapping
    public PagedModel<AgenteResponse> listar(Pageable pageable) {
        return agenteService.listar(pageable);
    }

    @GetMapping("/{id}")
    public AgenteResponse buscarPorId(@PathVariable Long id) {
        return agenteService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AgenteResponse atualizar(@PathVariable Long id, @RequestBody @Valid AgenteRequest agenteRequest) {
        return agenteService.atualizar(id, agenteRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        agenteService.delete(id);
    }
    
    
}
