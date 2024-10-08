package com.example.gateway.utils;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up RabbitMQ messaging components such as exchanges, queues, and bindings.
 *
 * <p>
 * This class configures the necessary RabbitMQ components using values from the application properties:
 * <ul>
 *   <li>{@code rabbitmq.routing-key}: The routing key used to bind the queue to the exchange.</li>
 *   <li>{@code rabbitmq.queue}: The name of the queue to which messages will be sent.</li>
 *   <li>{@code rabbitmq.exchange}: The name of the exchange to which the queue is bound.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The exchange is configured as a topic exchange, and the queue is durable, meaning it will survive a broker restart.
 * </p>
 * <p>
 * Example configuration in application.properties:
 * <pre>
 * rabbitmq.routing-key=my.routing.key
 * rabbitmq.queue=myQueue
 * rabbitmq.exchange=myExchange
 * </pre>
 * <p>
 * This configuration will create the exchange, queue, and binding when the application starts.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(exchangeName)
                .durable(true)
                .build();
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
                .noargs();
    }
}
