package com.test.rabbitmqproducer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = {"${rabbitmq.queue.main}"})
    public void consume(final String name) throws InvalidNameException {
        if (!Pattern.matches("[a-zA-Z]+", name)) {
            log.info("Retrying...");
            throw new InvalidNameException("Name should contain only alphabets");
        }

        log.info(String.format("Received message -> %s", name));
    }
}