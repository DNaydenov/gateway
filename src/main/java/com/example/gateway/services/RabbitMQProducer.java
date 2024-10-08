package com.example.gateway.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        System.out.println("Message sent to RabbitMQ: " + message);
    }
}
