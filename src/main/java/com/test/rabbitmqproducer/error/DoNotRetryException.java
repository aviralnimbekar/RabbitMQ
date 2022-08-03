package com.test.rabbitmqproducer.error;

public class DoNotRetryException extends Exception {

    public DoNotRetryException(String message) {
        super(message);
    }
}
