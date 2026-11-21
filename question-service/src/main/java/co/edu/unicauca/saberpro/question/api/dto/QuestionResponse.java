package co.edu.unicauca.saberpro.question.api.dto;

import co.edu.unicauca.saberpro.question.domain.model.Question;

import java.util.List;

public record QuestionResponse(Long id, String statement, List<AnswerOptionDto> options, String correctKey,
                               String justification, String status) {

    public static QuestionResponse from(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getStatement(),
                q.getOptions().stream().map(o -> new AnswerOptionDto(o.getKey(), o.getText())).toList(),
                q.getCorrectKey(),
                q.getJustification(),
                q.getStatus().name());
    }
}
