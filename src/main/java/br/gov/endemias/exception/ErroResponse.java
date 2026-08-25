package br.gov.endemias.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
    LocalDateTime timestamp,
    Integer status,
    String error,
    List<String> mensages
) {

}
