package co.edu.unicauca.saberpro.lifecycle.messaging;

import java.time.Instant;

/**
 * Copia local del comando (los microservicios no comparten codigo). El contrato es el JSON:
 * {"operationId": "...", "questionId": 1, "requestedAt": "..."}.
 */
public record SubmitQuestionForReview(String operationId, Long questionId, Instant requestedAt) {
}
