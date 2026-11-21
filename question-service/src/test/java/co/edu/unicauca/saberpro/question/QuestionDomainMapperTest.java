package co.edu.unicauca.saberpro.question;

import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionStatus;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.mapper.QuestionDomainMapper;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.record.QuestionRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class QuestionDomainMapperTest {

    private final QuestionDomainMapper mapper = new QuestionDomainMapper();

    private static Question sample() {
        return Question.newDraft("Enunciado",
                List.of(new AnswerOption("A", "uno"), new AnswerOption("B", "dos"),
                        new AnswerOption("C", "tres"), new AnswerOption("D", "cuatro")),
                "B", "Justificacion suficientemente larga.");
    }

    @Test
    void toRecordCopiesFieldsAndLinksOptionsBackToParent() {
        QuestionRecord record = mapper.toRecord(sample());
        assertNull(record.getId());
        assertEquals("DRAFT", record.getStatus());
        assertEquals(4, record.getOptions().size());
        record.getOptions().forEach(o -> assertSame(record, o.getQuestion()));
    }

    @Test
    void roundTripPreservesTheDomainObject() {
        Question original = sample();
        Question back = mapper.toDomain(mapper.toRecord(original));
        assertEquals(original.getStatement(), back.getStatement());
        assertEquals(original.getCorrectKey(), back.getCorrectKey());
        assertEquals(original.getJustification(), back.getJustification());
        assertEquals(QuestionStatus.DRAFT, back.getStatus());
        assertEquals(4, back.getOptions().size());
        assertEquals("C", back.getOptions().get(2).getKey());
        assertEquals("tres", back.getOptions().get(2).getText());
    }
}
