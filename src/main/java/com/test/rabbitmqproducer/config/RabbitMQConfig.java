package com.test.rabbitmqproducer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.main}")
    private String queue;

    @Value("${rabbitmq.exchange.main}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue.dlq}")
    private String dlQueue;

    @Value("${rabbitmq.exchange.dlq}")
    private String dlExchange;

    @Value("${rabbitmq.routing.dlKey}")
    private String dlRoutingKey;

    // spring bean for rabbitmq queue
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(queue)
                .withArgument("x-dead-letter-exchange", dlExchange)
                .withArgument("x-dead-letter-routing-key", dlRoutingKey)
                .build();
    }

    // spring bean for rabbitmq exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    // binding between queue and exchange using routing key
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    public Queue dlQueue() {
        return QueueBuilder.durable(dlQueue).build();
    }

    @Bean
    public DirectExchange dlExchange() {
        return new DirectExchange(dlExchange);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlQueue())
                .to(dlExchange())
                .with(dlRoutingKey);
    }

// Spring boot autoconfiguration provides following beans
    // ConnectionFactory
    // RabbitTemplate
    // RabbitAdmin
}