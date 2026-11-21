package co.edu.unicauca.saberpro.validation.api.dto;

import java.util.List;

public record ValidationResponse(boolean valid, List<String> errors) {

    public static ValidationResponse ok() {
        return new ValidationResponse(true, List.of());
    }

    public static ValidationResponse invalid(List<String> errors) {
        return new ValidationResponse(false, List.copyOf(errors));
    }
}
