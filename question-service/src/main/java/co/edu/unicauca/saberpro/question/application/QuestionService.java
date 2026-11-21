package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.question.application.exception.QuestionNotFoundException;
import co.edu.unicauca.saberpro.question.application.exception.QuestionValidationException;
import co.edu.unicauca.saberpro.question.application.port.SubmitCommandPublisher;
import co.edu.unicauca.saberpro.question.application.port.SubmitQuestionForReview;
import co.edu.unicauca.saberpro.question.application.port.ValidationGateway;
import co.edu.unicauca.saberpro.question.application.port.ValidationResult;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository repository;
    private final ValidationGateway validationGateway;
    private final SubmitCommandPublisher publisher;

    public QuestionService(QuestionRepository repository, ValidationGateway validationGateway,
                           SubmitCommandPublisher publisher) {
        this.repository = repository;
        this.validationGateway = validationGateway;
        this.publisher = publisher;
    }

    /** Punto 1: valida de forma sincrona (REST) y SOLO si es valida persiste la pregunta en DRAFT. */
    public Question create(Question draft) {
        ValidationResult result = validationGateway.validate(draft);
        if (!result.valid()) {
            log.info("Creacion cancelada, errores de validacion: {}", result.errors());
            throw new QuestionValidationException(result.errors());
        }
        Question saved = repository.save(draft);
        log.info("Pregunta {} creada en estado {}", saved.getId(), saved.getStatus());
        return saved;
    }

    public Question get(Long id) {
        return repository.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
    }

    /** Punto 3: publica el comando y retorna de inmediato; el cambio de estado lo hace Lifecycle. */
    public SubmitOperation submitForReview(Long id) {
        Question question = get(id);
        String operationId = UUID.randomUUID().toString();
        publisher.publish(new SubmitQuestionForReview(operationId, question.getId(), Instant.now()));
        log.info("Comando SubmitQuestionForReview publicado: operationId={}, questionId={}",
                operationId, question.getId());
        return new SubmitOperation(operationId, "PROCESSING");
    }
}
