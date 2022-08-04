package com.test.rabbitmqproducer.config;

import com.test.rabbitmqproducer.error.DoNotRetryException;
import com.test.rabbitmqproducer.error.RetryException;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryPolicyConfig {


    @Value("${rabbitmq.exchange.dlq}")
    private String dlExchange;

    @Value("${rabbitmq.routing.dlKey}")
    private String dlRoutingKey;

    // Needed for serializing incoming rabbit messages
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRetryPolicy rejectionRetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionsMap = new HashMap<>();
        exceptionsMap.put(DoNotRetryException.class, false);//not retriable
        exceptionsMap.put(RetryException.class, true); //retriable
        return new SimpleRetryPolicy(3, exceptionsMap, true);
    }

    @Bean
    RetryOperationsInterceptor interceptor() {
        return RetryInterceptorBuilder.stateless()
                .retryPolicy(rejectionRetryPolicy())
                .backOffOptions(2000L, 2, 3000L)
                .recoverer(
                        new RepublishMessageRecoverer(rabbitTemplate(), dlExchange, dlRoutingKey))
                .build();
    }

    @Bean()
    SimpleRabbitListenerContainerFactory customConnectionfactory(CachingConnectionFactory cf1) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf1);
        factory.setAdviceChain(interceptor());
        factory.setMessageConverter(converter());
        return factory;
    }

    @Bean
    CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory();
    }

    @Bean
    RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
