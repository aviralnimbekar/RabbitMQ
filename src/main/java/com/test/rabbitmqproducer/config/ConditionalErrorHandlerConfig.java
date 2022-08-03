package com.test.rabbitmqproducer.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionalErrorHandlerConfig {
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
//                                                                               SimpleRabbitListenerContainerFactoryConfigurer configurer) {
//
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        configurer.configure(factory, connectionFactory);
//        factory.setErrorHandler(errorHandler());
//        return factory;
//    }
//
//    @Bean
//    public ErrorHandler errorHandler() {
//        return new ConditionalRejectingErrorHandler(customExceptionStrategy());
//    }
//
//    @Bean
//    FatalExceptionStrategy customExceptionStrategy() {
//        return new CustomFatalExceptionStrategy();
//    }
}
