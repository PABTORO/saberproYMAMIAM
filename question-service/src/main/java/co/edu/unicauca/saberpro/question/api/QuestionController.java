package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.api.dto.AnswerOptionDto;
import co.edu.unicauca.saberpro.question.api.dto.CreateQuestionRequest;
import co.edu.unicauca.saberpro.question.api.dto.QuestionResponse;
import co.edu.unicauca.saberpro.question.api.dto.SubmitResponse;
import co.edu.unicauca.saberpro.question.application.QuestionService;
import co.edu.unicauca.saberpro.question.application.SubmitOperation;
import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    /** 201 Created si la validacion es exitosa; 400 Bad Request (ver GlobalExceptionHandler) si falla. */
    @PostMapping
    public ResponseEntity<QuestionResponse> create(@RequestBody CreateQuestionRequest request) {
        Question created = service.create(toDomain(request));
        return ResponseEntity
                .created(URI.create("/questions/" + created.getId()))
                .body(QuestionResponse.from(created));
    }

    @GetMapping("/{id}")
    public QuestionResponse get(@PathVariable Long id) {
        return QuestionResponse.from(service.get(id));
    }

    /** 202 Accepted inmediato: el cambio de estado lo procesa el Lifecycle Service en segundo plano. */
    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmitResponse> submit(@PathVariable Long id) {
        SubmitOperation operation = service.submitForReview(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new SubmitResponse(operation.operationId(), operation.status()));
    }

    private static Question toDomain(CreateQuestionRequest request) {
        List<AnswerOption> options = new ArrayList<>();
        if (request.options() != null) {
            for (AnswerOptionDto dto : request.options()) {
                // una opcion nula se transforma en opcion vacia para que el Validation Service la reporte
                options.add(dto == null ? new AnswerOption(null, null) : new AnswerOption(dto.key(), dto.text()));
            }
        }
        return Question.newDraft(request.statement(), options, request.correctKey(), request.justification());
    }
}
