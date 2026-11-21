package co.edu.unicauca.saberpro.question.api.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(int status, String message, List<String> errors, Instant timestamp) {

    public static ApiError of(int status, String message, List<String> errors) {
        return new ApiError(status, message, errors, Instant.now());
    }
}
