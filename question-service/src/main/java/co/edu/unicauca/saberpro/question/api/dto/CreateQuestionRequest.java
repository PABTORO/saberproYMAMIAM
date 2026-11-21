package co.edu.unicauca.saberpro.question.api.dto;

import java.util.List;

public record CreateQuestionRequest(String statement, List<AnswerOptionDto> options, String correctKey,
                                    String justification) {
}
