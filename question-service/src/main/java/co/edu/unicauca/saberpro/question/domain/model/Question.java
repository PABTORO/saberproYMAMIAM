package co.edu.unicauca.saberpro.question.domain.model;

import java.util.Collections;
import java.util.List;

/**
 * Modelo de dominio puro (Java plano): SIN @Entity, @Table ni @Id.
 * El id es null hasta que la capa de persistencia lo asigna.
 */
public class Question {

    private final Long id;
    private final String statement;
    private final List<AnswerOption> options;
    private final String correctKey;
    private final String justification;
    private final QuestionStatus status;

    public Question(Long id, String statement, List<AnswerOption> options, String correctKey,
                    String justification, QuestionStatus status) {
        this.id = id;
        this.statement = statement;
        this.options = options == null ? List.of() : Collections.unmodifiableList(List.copyOf(options));
        this.correctKey = correctKey;
        this.justification = justification;
        this.status = status;
    }

    /** Fabrica de una pregunta nueva: sin id y en estado DRAFT. */
    public static Question newDraft(String statement, List<AnswerOption> options, String correctKey,
                                    String justification) {
        return new Question(null, statement, options, correctKey, justification, QuestionStatus.DRAFT);
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public String getCorrectKey() {
        return correctKey;
    }

    public String getJustification() {
        return justification;
    }

    public QuestionStatus getStatus() {
        return status;
    }
}
