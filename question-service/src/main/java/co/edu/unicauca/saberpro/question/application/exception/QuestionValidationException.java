package co.edu.unicauca.saberpro.question.application.exception;

import java.util.List;

public class QuestionValidationException extends RuntimeException {

    private final List<String> errors;

    public QuestionValidationException(List<String> errors) {
        super("La pregunta no supero la validacion estructural");
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
