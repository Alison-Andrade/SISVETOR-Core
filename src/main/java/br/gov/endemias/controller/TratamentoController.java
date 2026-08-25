package br.gov.endemias.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.TratamentoRequest;
import br.gov.endemias.dto.TratamentoResponse;
import br.gov.endemias.service.TratamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tratamentos")
@RequiredArgsConstructor
public class TratamentoController {
    
    private final TratamentoService tratamentoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TratamentoResponse cadastrar(@RequestBody @Valid TratamentoRequest request) {
        return tratamentoService.cadastrar(request);
    }

    @GetMapping
    public PagedModel<TratamentoResponse> listar(Pageable pageable) {
        return tratamentoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public TratamentoResponse buscarPorId(@PathVariable Long id) {
        return tratamentoService.buscarPorId(id);
    }
    
}
