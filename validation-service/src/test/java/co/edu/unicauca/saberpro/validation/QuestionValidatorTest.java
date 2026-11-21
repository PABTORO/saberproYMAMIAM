package co.edu.unicauca.saberpro.validation;

import co.edu.unicauca.saberpro.validation.api.dto.OptionDto;
import co.edu.unicauca.saberpro.validation.api.dto.QuestionValidationRequest;
import co.edu.unicauca.saberpro.validation.domain.QuestionValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionValidatorTest {

    private static final String GOOD_JUSTIFICATION =
            "La opcion B es correcta porque aplica la definicion vista en clase.";

    private final QuestionValidator validator = new QuestionValidator();

    private static List<OptionDto> four() {
        return List.of(new OptionDto("A", "uno"), new OptionDto("B", "dos"),
                new OptionDto("C", "tres"), new OptionDto("D", "cuatro"));
    }

    @Test
    void validQuestionHasNoErrors() {
        var req = new QuestionValidationRequest("Enunciado", four(), "B", GOOD_JUSTIFICATION);
        assertTrue(validator.validate(req).isEmpty());
    }

    @Test
    void threeOptionsIsRejected() {
        var req = new QuestionValidationRequest("Enunciado", four().subList(0, 3), "B", GOOD_JUSTIFICATION);
        List<String> errors = validator.validate(req);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("exactamente 4 opciones"));
    }

    @Test
    void unknownCorrectKeyIsRejected() {
        var req = new QuestionValidationRequest("Enunciado", four(), "Z", GOOD_JUSTIFICATION);
        List<String> errors = validator.validate(req);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("clave de respuesta"));
    }

    @Test
    void shortJustificationIsRejected() {
        var req = new QuestionValidationRequest("Enunciado", four(), "B", "corta");
        List<String> errors = validator.validate(req);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("justificacion"));
    }

    @Test
    void nullFieldsProduceSeveralErrorsWithoutException() {
        var req = new QuestionValidationRequest(null, null, null, null);
        assertEquals(4, validator.validate(req).size());
    }
}
