package co.edu.unicauca.saberpro.question.infrastructure.jpa.record;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/** Entidad relacional JPA. Vive solo en infraestructura; el dominio no la conoce. */
@Entity
@Table(name = "questions")
public class QuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String statement;

    @Column(name = "correct_key", nullable = false, length = 50)
    private String correctKey;

    @Column(nullable = false, length = 1000)
    private String justification;

    @Column(nullable = false, length = 30)
    private String status;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<AnswerOptionRecord> options = new ArrayList<>();

    protected QuestionRecord() {
        // requerido por JPA
    }

    public QuestionRecord(Long id, String statement, String correctKey, String justification, String status) {
        this.id = id;
        this.statement = statement;
        this.correctKey = correctKey;
        this.justification = justification;
        this.status = status;
    }

    /** Mantiene sincronizados ambos lados de la relacion bidireccional. */
    public void addOption(AnswerOptionRecord option) {
        options.add(option);
        option.setQuestion(this);
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public String getCorrectKey() {
        return correctKey;
    }

    public String getJustification() {
        return justification;
    }

    public String getStatus() {
        return status;
    }

    public List<AnswerOptionRecord> getOptions() {
        return options;
    }
}
