package co.edu.unicauca.saberpro.lifecycle.messaging;

import co.edu.unicauca.saberpro.lifecycle.domain.QuestionStateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consumidor en segundo plano del comando SubmitQuestionForReview. */
@Component
public class SubmitQuestionListener {

    private static final Logger log = LoggerFactory.getLogger(SubmitQuestionListener.class);

    private final QuestionStateStore store;

    public SubmitQuestionListener(QuestionStateStore store) {
        this.store = store;
    }

    @RabbitListener(queues = RabbitTopology.QUEUE)
    public void onSubmitQuestionForReview(SubmitQuestionForReview command) {
        log.info("Comando recibido: operationId={}, questionId={}", command.operationId(), command.questionId());
        if (command.questionId() == null) {
            log.warn("Comando {} descartado: questionId nulo", command.operationId());
            return;
        }
        boolean transitioned = store.submitForReview(command.questionId());
        if (transitioned) {
            log.info("Pregunta {}: DRAFT -> PENDING_REVIEW (operationId={})",
                    command.questionId(), command.operationId());
        } else {
            log.info("Pregunta {} ya estaba en PENDING_REVIEW; comando {} ignorado (idempotente)",
                    command.questionId(), command.operationId());
        }
    }
}
