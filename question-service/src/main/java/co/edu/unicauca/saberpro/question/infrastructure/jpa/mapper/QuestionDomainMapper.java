package co.edu.unicauca.saberpro.question.infrastructure.jpa.mapper;

import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionStatus;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.record.AnswerOptionRecord;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.record.QuestionRecord;
import org.springframework.stereotype.Component;

/** Mapeador bidireccional Question <-> QuestionRecord (incluye sus opciones). */
@Component
public class QuestionDomainMapper {

    public QuestionRecord toRecord(Question question) {
        QuestionRecord record = new QuestionRecord(
                question.getId(),
                question.getStatement(),
                question.getCorrectKey(),
                question.getJustification(),
                question.getStatus().name());
        for (AnswerOption option : question.getOptions()) {
            record.addOption(new AnswerOptionRecord(option.getKey(), option.getText()));
        }
        return record;
    }

    public Question toDomain(QuestionRecord record) {
        return new Question(
                record.getId(),
                record.getStatement(),
                record.getOptions().stream()
                        .map(o -> new AnswerOption(o.getKey(), o.getText()))
                        .toList(),
                record.getCorrectKey(),
                record.getJustification(),
                QuestionStatus.valueOf(record.getStatus()));
    }
}
