package co.edu.unicauca.saberpro.question.application.port;

/** Puerto de salida para publicar comandos de revision. */
public interface SubmitCommandPublisher {

    void publish(SubmitQuestionForReview command);
}
