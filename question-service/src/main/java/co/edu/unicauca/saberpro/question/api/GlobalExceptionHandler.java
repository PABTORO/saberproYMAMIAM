package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.api.dto.ApiError;
import co.edu.unicauca.saberpro.question.application.exception.QuestionNotFoundException;
import co.edu.unicauca.saberpro.question.application.exception.QuestionValidationException;
import co.edu.unicauca.saberpro.question.application.exception.ValidationServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(QuestionValidationException.class)
    public ResponseEntity<ApiError> handleValidation(QuestionValidationException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of(400, "Validacion fallida", ex.getErrors()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiError.of(400, "Solicitud malformada",
                List.of("Cuerpo JSON ausente/malformado o parametro de ruta con formato invalido")));
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(QuestionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(ValidationServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleValidationDown(ValidationServiceUnavailableException ex) {
        log.error("Validation Service no disponible", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiError.of(503, ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ApiError> handleBroker(AmqpException ex) {
        log.error("RabbitMQ no disponible", ex);
        String message = "No fue posible publicar el comando en RabbitMQ";
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiError.of(503, message, List.of(message)));
    }
}
