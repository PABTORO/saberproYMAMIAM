package co.edu.unicauca.saberpro.question.domain.model;

/** Opcion de respuesta. Modelo de dominio puro: sin anotaciones de persistencia. */
public final class AnswerOption {

    private final String key;
    private final String text;

    public AnswerOption(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }
}
