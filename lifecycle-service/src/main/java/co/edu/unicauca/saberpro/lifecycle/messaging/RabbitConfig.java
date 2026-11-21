package co.edu.unicauca.saberpro.lifecycle.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    /**
     * Convierte JSON -> objeto. El tipo destino se infiere del parametro del @RabbitListener, por lo que
     * no depende de la clase del Question Service indicada en el header __TypeId__.
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
