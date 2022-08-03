package com.test.rabbitmqproducer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer.*;

@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements RabbitListenerErrorHandler {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message,
                              ListenerExecutionFailedException exception) {

//        var augmentedMessage = augmentMessage(amqpMessage, exception.getCause());
        rabbitTemplate.send("dlExchange", "dlRoutingKey", amqpMessage);

        throw new AmqpRejectAndDontRequeueException("GlobalListenerExceptionHandler thrown AmqpRejectAndDontRequeueException");
    }

    // copied and modified from RepublishMessageRecoverer.recover
    private Message augmentMessage(Message message, Throwable cause) {
        var messageProperties = message.getMessageProperties();
        Map<String, Object> headers = messageProperties.getHeaders();
        String exceptionMessage = cause.getCause() != null ? cause.getCause().getMessage() : cause.getMessage();
        headers.put(X_EXCEPTION_MESSAGE, exceptionMessage);
        headers.put(X_ORIGINAL_EXCHANGE, messageProperties.getReceivedExchange());
        headers.put(X_ORIGINAL_ROUTING_KEY, messageProperties.getReceivedRoutingKey());
        headers.put("x-consuming-queue", messageProperties.getConsumerQueue());
        headers.put("x-target-method", messageProperties.getTargetMethod().toString());
        headers.put("x-meta", "Augmented failed message");

        return message;
    }
}