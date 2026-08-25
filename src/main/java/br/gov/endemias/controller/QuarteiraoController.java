package br.gov.endemias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.QuarteiraoDetalhadoResponse;
import br.gov.endemias.dto.QuarteiraoRequest;
import br.gov.endemias.dto.QuarteiraoResponse;
import br.gov.endemias.service.QuarteiraoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/v1/quarteiroes")
@RequiredArgsConstructor
public class QuarteiraoController {
    
    private final QuarteiraoService quarteiraoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuarteiraoResponse cadastrar(@RequestBody @Valid QuarteiraoRequest request) {
        return quarteiraoService.cadastrar(request);
    }

    @GetMapping("/localidade/{localidadeId}")
    public List<QuarteiraoResponse> listarPorLocalidade(@PathVariable Long localidadeId) {
        return quarteiraoService.listarPorLocalidade(localidadeId);
    }

    @GetMapping("/{id}/detalhado")
    public QuarteiraoDetalhadoResponse listarDetalhadoPorId(@PathVariable Long id) {
        return quarteiraoService.buscarDetalhadoPorId(id);
    }

    @PutMapping("/{id}")
    public QuarteiraoResponse atualizar(@PathVariable Long id, @RequestBody @Valid QuarteiraoRequest request) {
        return quarteiraoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        quarteiraoService.deletar(id);
    }
    

}
