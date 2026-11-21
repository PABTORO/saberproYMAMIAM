package co.edu.unicauca.saberpro.question.infrastructure.jpa.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer_options")
public class AnswerOptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "key" es palabra reservada en H2, por eso el nombre de columna explicito.
    @Column(name = "option_key", nullable = false, length = 50)
    private String key;

    @Column(name = "option_text", nullable = false, length = 500)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionRecord question;

    protected AnswerOptionRecord() {
        // requerido por JPA
    }

    public AnswerOptionRecord(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public QuestionRecord getQuestion() {
        return question;
    }

    void setQuestion(QuestionRecord question) {
        this.question = question;
    }
}
