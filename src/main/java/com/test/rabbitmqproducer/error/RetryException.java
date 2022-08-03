package com.test.rabbitmqproducer.error;

public class RetryException extends Exception {

    public RetryException(String message) {
        super(message);
    }
}
