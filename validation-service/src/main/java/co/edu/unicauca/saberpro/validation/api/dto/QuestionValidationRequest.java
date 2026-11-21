package co.edu.unicauca.saberpro.validation.api.dto;

import java.util.List;

/** Cuerpo de POST /validate. Todos los campos pueden venir nulos: el validador reporta el error. */
public record QuestionValidationRequest(
        String statement,
        List<OptionDto> options,
        String correctKey,
        String justification) {
}
