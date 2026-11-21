package co.edu.unicauca.saberpro.question;

import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** El dominio debe ser Java puro: ninguna anotacion (JPA u otra) en clases ni campos. */
class DomainIsolationTest {

    @Test
    void domainClassesHaveNoAnnotations() {
        for (Class<?> type : new Class<?>[] {Question.class, AnswerOption.class}) {
            assertEquals(0, type.getAnnotations().length, type.getSimpleName() + " tiene anotaciones");
            for (Field field : type.getDeclaredFields()) {
                assertEquals(0, field.getAnnotations().length,
                        type.getSimpleName() + "." + field.getName() + " tiene anotaciones");
            }
        }
    }
}
