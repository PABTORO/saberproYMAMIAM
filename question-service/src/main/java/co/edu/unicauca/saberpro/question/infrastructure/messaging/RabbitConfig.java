package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara exchange, cola y binding (idempotente). Se declaran tambien aqui para que el comando
 * quede encolado aunque el Lifecycle Service aun no haya arrancado.
 */
@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange saberproExchange() {
        return new DirectExchange(RabbitTopology.EXCHANGE, true, false);
    }

    @Bean
    public Queue submitReviewQueue() {
        return new Queue(RabbitTopology.QUEUE, true);
    }

    @Bean
    public Binding submitReviewBinding(Queue submitReviewQueue, DirectExchange saberproExchange) {
        return BindingBuilder.bind(submitReviewQueue).to(saberproExchange).with(RabbitTopology.ROUTING_KEY);
    }

    /** Serializa los comandos a JSON (usa el ObjectMapper de Spring Boot, con soporte de Instant). */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
