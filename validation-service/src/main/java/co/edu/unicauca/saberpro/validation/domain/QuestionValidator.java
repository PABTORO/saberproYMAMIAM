package co.edu.unicauca.saberpro.validation.domain;

import co.edu.unicauca.saberpro.validation.api.dto.OptionDto;
import co.edu.unicauca.saberpro.validation.api.dto.QuestionValidationRequest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reglas de validacion estructural (HU03). Clase Java pura, sin dependencias de Spring.
 *
 * Reglas:
 *  1. Enunciado obligatorio.
 *  2. Exactamente 4 opciones, cada una con clave y texto, sin claves repetidas.
 *  3. La clave de respuesta debe existir entre las claves de las opciones (comparacion exacta).
 *  4. Justificacion pedagogica: obligatoria, entre 20 y 500 caracteres (sin contar espacios extremos).
 */
public class QuestionValidator {

    public static final int REQUIRED_OPTIONS = 4;
    public static final int JUSTIFICATION_MIN_LENGTH = 20;
    public static final int JUSTIFICATION_MAX_LENGTH = 500;

    public List<String> validate(QuestionValidationRequest request) {
        List<String> errors = new ArrayList<>();
        if (request == null) {
            errors.add("La solicitud no puede ser nula");
            return errors;
        }

        if (isBlank(request.statement())) {
            errors.add("El enunciado de la pregunta es obligatorio");
        }

        Set<String> keys = new LinkedHashSet<>();
        List<OptionDto> options = request.options();
        if (options == null) {
            errors.add("La pregunta debe tener exactamente " + REQUIRED_OPTIONS
                    + " opciones de respuesta (no se recibieron opciones)");
        } else {
            if (options.size() != REQUIRED_OPTIONS) {
                errors.add("La pregunta debe tener exactamente " + REQUIRED_OPTIONS
                        + " opciones de respuesta (se recibieron " + options.size() + ")");
            }
            for (int i = 0; i < options.size(); i++) {
                OptionDto option = options.get(i);
                int position = i + 1;
                if (option == null) {
                    errors.add("La opcion #" + position + " es nula");
                    continue;
                }
                if (isBlank(option.key())) {
                    errors.add("La opcion #" + position + " no tiene clave");
                } else if (!keys.add(option.key())) {
                    errors.add("La clave de opcion '" + option.key() + "' esta duplicada");
                }
                if (isBlank(option.text())) {
                    errors.add("La opcion #" + position + " no tiene texto");
                }
            }
        }

        if (isBlank(request.correctKey())) {
            errors.add("La clave de respuesta es obligatoria");
        } else if (!keys.contains(request.correctKey())) {
            errors.add("La clave de respuesta '" + request.correctKey()
                    + "' no existe entre las opciones (claves disponibles: " + keys + ")");
        }

        if (isBlank(request.justification())) {
            errors.add("La justificacion es obligatoria");
        } else {
            int length = request.justification().trim().length();
            if (length < JUSTIFICATION_MIN_LENGTH) {
                errors.add("La justificacion debe tener al menos " + JUSTIFICATION_MIN_LENGTH
                        + " caracteres (tiene " + length + ")");
            } else if (length > JUSTIFICATION_MAX_LENGTH) {
                errors.add("La justificacion no puede superar " + JUSTIFICATION_MAX_LENGTH
                        + " caracteres (tiene " + length + ")");
            }
        }
        return errors;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
