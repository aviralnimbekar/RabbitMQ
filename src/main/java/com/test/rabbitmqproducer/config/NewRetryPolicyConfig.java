package com.test.rabbitmqproducer.config;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
public class NewRetryPolicyConfig {

    @Value("${rabbitmq.exchange.dlq}")
    private String dlExchange;

    @Bean
    RetryOperationsInterceptor newInterceptor(SimpleRetryPolicy rejectionRetryPolicy, RabbitTemplate rabbitTemplate) {
        return RetryInterceptorBuilder.stateless()
                .retryPolicy(rejectionRetryPolicy)
                .backOffOptions(2000L, 2, 3000L)
                .recoverer(
                        new RepublishMessageRecoverer(rabbitTemplate, dlExchange, ""))
                .build();
    }

    @Bean()
    SimpleRabbitListenerContainerFactory newCustomConnectionfactory(CachingConnectionFactory cf1, SimpleRetryPolicy rejectionRetryPolicy,
                                                                    RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf1);
        factory.setAdviceChain(newInterceptor(rejectionRetryPolicy, rabbitTemplate));
        factory.setMessageConverter(converter);
        return factory;
    }

}
