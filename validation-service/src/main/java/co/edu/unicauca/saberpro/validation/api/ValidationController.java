package co.edu.unicauca.saberpro.validation.api;

import co.edu.unicauca.saberpro.validation.api.dto.QuestionValidationRequest;
import co.edu.unicauca.saberpro.validation.api.dto.ValidationResponse;
import co.edu.unicauca.saberpro.validation.domain.QuestionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ValidationController {

    private static final Logger log = LoggerFactory.getLogger(ValidationController.class);

    private final QuestionValidator validator;

    public ValidationController(QuestionValidator validator) {
        this.validator = validator;
    }

    /** 200 OK si la pregunta es valida; 400 Bad Request con la lista de errores si no lo es. */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@RequestBody QuestionValidationRequest request) {
        List<String> errors = validator.validate(request);
        if (errors.isEmpty()) {
            log.info("Pregunta valida");
            return ResponseEntity.ok(ValidationResponse.ok());
        }
        log.info("Pregunta invalida: {}", errors);
        return ResponseEntity.badRequest().body(ValidationResponse.invalid(errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                ValidationResponse.invalid(List.of("Cuerpo de la solicitud ausente o JSON malformado")));
    }
}
