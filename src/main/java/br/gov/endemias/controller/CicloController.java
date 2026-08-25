package br.gov.endemias.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;

import br.gov.endemias.dto.CicloRequest;
import br.gov.endemias.dto.CicloResponse;
import br.gov.endemias.service.CicloService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/ciclos")
@RequiredArgsConstructor
public class CicloController {
    
    private final CicloService cicloService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CicloResponse cadastrar(@RequestBody @Valid CicloRequest request) {
        return cicloService.cadastrar(request);
    }

    @GetMapping
    public PagedModel<CicloResponse> listarCiclos(Pageable pageable) {
        return cicloService.listarCiclos(pageable);
    }

    @GetMapping("/aberto")
    public CicloResponse buscarAberto() {
        return cicloService.buscarCicloAberto();
    }

    @GetMapping("/{id}")
    public CicloResponse buscarPorId(@PathVariable Long id) {
        return cicloService.buscarCicloPorId(id);
    }

    @PutMapping("/concluir")
    public CicloResponse concluirCicloAtual() {
        return cicloService.concluirCiclo();
    }
    
    @PutMapping("/{id}/reabrir")
    public CicloResponse reabrirCiclo(@PathVariable Long id) {
        return cicloService.reabrirCiclo(id);
    }

    @DeleteMapping("/{id}")
    public void deletarCiclo(@PathVariable Long id) {
        cicloService.deletarCiclo(id);
    }
}
