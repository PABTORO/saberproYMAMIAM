package co.edu.unicauca.saberpro.question.application.port;

import java.time.Instant;

/** Comando asincrono publicado en RabbitMQ para solicitar el paso DRAFT -> PENDING_REVIEW. */
public record SubmitQuestionForReview(String operationId, Long questionId, Instant requestedAt) {
}
