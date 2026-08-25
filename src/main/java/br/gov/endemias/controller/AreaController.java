package br.gov.endemias.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;

import br.gov.endemias.dto.AreaRequest;
import br.gov.endemias.dto.AreaResponse;
import br.gov.endemias.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/v1/area")
@RequiredArgsConstructor
public class AreaController {
    
    private final AreaService areaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AreaResponse cadastrar(@RequestBody AreaRequest request) {
        return areaService.cadastrar(request);
    }

    @GetMapping
    public List<AreaResponse> listar() {
        return areaService.listar();
    }

    @GetMapping("/{id}")
    public AreaResponse buscarPorId(@PathVariable Long id) {
        return areaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AreaResponse atualizar(@PathVariable Long id, @RequestBody AreaRequest request) {
        return areaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        areaService.deletar(id);
    }
}
