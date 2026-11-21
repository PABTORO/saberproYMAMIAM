package co.edu.unicauca.saberpro.question.application.port;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
}
