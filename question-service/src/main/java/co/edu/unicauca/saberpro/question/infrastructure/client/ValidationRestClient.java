package co.edu.unicauca.saberpro.question.infrastructure.client;

import co.edu.unicauca.saberpro.question.application.exception.ValidationServiceUnavailableException;
import co.edu.unicauca.saberpro.question.application.port.ValidationGateway;
import co.edu.unicauca.saberpro.question.application.port.ValidationResult;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Invocacion sincrona HTTP REST a POST {validation-base-url}/validate usando RestTemplate.
 * El RestTemplate (ver RestClientConfig) no lanza excepcion ante 4xx/5xx: aqui se interpreta el status.
 */
@Component
public class ValidationRestClient implements ValidationGateway {

    private static final Logger log = LoggerFactory.getLogger(ValidationRestClient.class);

    private final RestTemplate restTemplate;
    private final String validateUrl;

    public ValidationRestClient(RestTemplate restTemplate,
                                @Value("${saberpro.validation.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.validateUrl = baseUrl + "/validate";
    }

    @Override
    public ValidationResult validate(Question question) {
        ValidationRequestBody body = new ValidationRequestBody(
                question.getStatement(),
                question.getOptions().stream()
                        .map(o -> new OptionBody(o.getKey(), o.getText()))
                        .toList(),
                question.getCorrectKey(),
                question.getJustification());

        ResponseEntity<ValidationResponseBody> response;
        try {
            response = restTemplate.postForEntity(validateUrl, body, ValidationResponseBody.class);
        } catch (RestClientException ex) {
            throw new ValidationServiceUnavailableException(
                    "No fue posible comunicarse con el Validation Service en " + validateUrl, ex);
        }

        log.info("POST {} -> {}", validateUrl, response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.OK) {
            return new ValidationResult(true, List.of());
        }
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            ValidationResponseBody payload = response.getBody();
            List<String> errors = (payload == null || payload.errors() == null || payload.errors().isEmpty())
                    ? List.of("El Validation Service rechazo la pregunta sin detalle")
                    : payload.errors();
            return new ValidationResult(false, errors);
        }
        throw new ValidationServiceUnavailableException(
                "Respuesta inesperada del Validation Service: " + response.getStatusCode(), null);
    }

    public record OptionBody(String key, String text) {
    }

    public record ValidationRequestBody(String statement, List<OptionBody> options, String correctKey,
                                        String justification) {
    }

    public record ValidationResponseBody(boolean valid, List<String> errors) {
    }
}
