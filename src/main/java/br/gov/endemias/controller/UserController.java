package br.gov.endemias.controller;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.endemias.dto.CadastroAdministrativoRequest;
import br.gov.endemias.dto.UserResponse;
import br.gov.endemias.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse cadastrar(@RequestBody @Valid CadastroAdministrativoRequest request,
                                  Authentication autenticacao) {
        return userService.cadastrarPorCoordenador(request, autenticacao);
    }

    @GetMapping("/pendentes")
    public PagedModel<UserResponse> listarPendentes(Pageable pageable) {
        return userService.listarPendentes(pageable);
    }

    @PutMapping("/{id}/aprovar")
    public UserResponse aprovar(@PathVariable Long id) {
        return userService.aprovar(id);
    }

    @PutMapping("/{id}/bloquear")
    public UserResponse bloquear(@PathVariable Long id, Authentication autenticacao) {
        return userService.bloquear(id, autenticacao);
    }

    @PutMapping("/{id}/desbloquear")
    public UserResponse desbloquear(@PathVariable Long id, Authentication autenticacao) {
        return userService.desbloquear(id, autenticacao);
    }
}
