package co.edu.unicauca.saberpro.lifecycle.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro centralizado del estado de cada pregunta (autoridad del ciclo de vida).
 * Una pregunta que este servicio aun no conoce se considera en DRAFT, porque el Question Service
 * siempre crea las preguntas en DRAFT. Almacenamiento en memoria (decision documentada en el README).
 */
public class QuestionStateStore {

    private final Map<Long, QuestionState> states = new ConcurrentHashMap<>();

    public QuestionState current(Long questionId) {
        return states.getOrDefault(questionId, QuestionState.DRAFT);
    }

    /**
     * Aplica DRAFT -> PENDING_REVIEW. Devuelve true si hubo transicion, false si la pregunta ya estaba
     * en PENDING_REVIEW (el comando se trata como idempotente: una re-entrega no rompe el flujo).
     */
    public boolean submitForReview(Long questionId) {
        boolean[] transitioned = {false};
        states.compute(questionId, (id, state) -> {
            QuestionState current = state == null ? QuestionState.DRAFT : state;
            if (current == QuestionState.DRAFT) {
                transitioned[0] = true;
                return QuestionState.PENDING_REVIEW;
            }
            return current;
        });
        return transitioned[0];
    }

    public Map<Long, QuestionState> snapshot() {
        return Map.copyOf(states);
    }

    public boolean isKnown(Long questionId) {
        return states.containsKey(questionId);
    }
}
