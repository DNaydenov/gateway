package com.example.gateway.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RabbitMQProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQProducer rabbitMQProducer;

    private final String exchangeName = "test-exchange";
    private final String routingKey = "test-routing-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize the mocks
        rabbitMQProducer = new RabbitMQProducer(rabbitTemplate);

        ReflectionTestUtils.setField(rabbitMQProducer, "exchangeName", exchangeName);
        ReflectionTestUtils.setField(rabbitMQProducer, "routingKey", routingKey);
    }

    @Test
    void testSendMessage() {
        String message = "Test Message";

        rabbitMQProducer.sendMessage(message);

        verify(rabbitTemplate, times(1)).convertAndSend(exchangeName, routingKey, message);
    }
}
