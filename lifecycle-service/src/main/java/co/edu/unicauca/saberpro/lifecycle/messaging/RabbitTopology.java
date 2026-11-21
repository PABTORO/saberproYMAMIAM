package co.edu.unicauca.saberpro.lifecycle.messaging;

public final class RabbitTopology {

    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String ROUTING_KEY = "question.submit";
    public static final String QUEUE = "question.submit.review.queue";

    private RabbitTopology() {
    }
}
