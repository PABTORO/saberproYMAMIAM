package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.question.application.port.SubmitCommandPublisher;
import co.edu.unicauca.saberpro.question.application.port.SubmitQuestionForReview;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitSubmitCommandPublisher implements SubmitCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitSubmitCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(SubmitQuestionForReview command) {
        rabbitTemplate.convertAndSend(RabbitTopology.EXCHANGE, RabbitTopology.ROUTING_KEY, command);
    }
}
