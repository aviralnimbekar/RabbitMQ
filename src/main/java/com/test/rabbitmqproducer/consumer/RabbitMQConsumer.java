package com.test.rabbitmqproducer.consumer;

import com.test.rabbitmqproducer.error.RetryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.regex.Pattern;

@Slf4j
@Service
@RabbitListener(queues = {"${rabbitmq.queue.main}"},
        containerFactory = "rabbitListenerContainerFactory")
public class RabbitMQConsumer {

    @RabbitHandler
    public void consume(final String name) throws InvalidNameException, RetryException {

        if (!Pattern.matches("[a-zA-Z]+", name)) {
            log.info("sending into dlq...");
            throw new InvalidNameException("Name should contain only alphabets");
        } else if ("Retry".equalsIgnoreCase(name)) {
            log.info("Retrying message...");
            throw new RetryException("Throwing Custom exception");
        }

        log.info(String.format("Received message -> %s", name));
    }
}