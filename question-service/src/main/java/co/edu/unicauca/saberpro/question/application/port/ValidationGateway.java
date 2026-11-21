package co.edu.unicauca.saberpro.question.application.port;

import co.edu.unicauca.saberpro.question.domain.model.Question;

/** Puerto de salida hacia el Validation Service. */
public interface ValidationGateway {

    ValidationResult validate(Question question);
}
