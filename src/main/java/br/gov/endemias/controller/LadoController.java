package br.gov.endemias.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.LadoRequest;
import br.gov.endemias.dto.LadoResponse;
import br.gov.endemias.service.LadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;



@RestController
@RequestMapping("api/v1/lados")
@RequiredArgsConstructor
public class LadoController {

    private final LadoService ladoService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LadoResponse cadastrar(@RequestBody LadoRequest request) {
        return ladoService.cadastrar(request);
    }

    @GetMapping("/{quarteiraoId}")
    public List<LadoResponse> listar(@PathVariable Long quarteiraoId) {
        return ladoService.listarPorQuarteirao(quarteiraoId);
    }

    @GetMapping("/{id}")
    public LadoResponse buscarPorId(Long id) {
        // return ladoService.buscarPorId(id);
        throw new RuntimeException("TO-DO");
    }

    @PutMapping("/{id}")
    public LadoResponse atualizar(Long id, LadoRequest request) {
        // return ladoService.atualizar(id, request);
        throw new RuntimeException("TO-DO");
    }
    
    @DeleteMapping("/{id}")
    public void deletar(Long id) {
        ladoService.deletar(id);
    }

}
