package com.test.rabbitmqproducer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(String message) {

        if ("exception".equals(message)) {
            throw new RuntimeException("Custom Exception");
        }

        log.info(String.format("Received message -> %s", message));
    }
}