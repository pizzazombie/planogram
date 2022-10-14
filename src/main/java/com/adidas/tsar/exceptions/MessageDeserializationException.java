package com.adidas.tsar.exceptions;

/**
 * Thrown in case Kafka message is failed to deserialize
 */
public class MessageDeserializationException extends RuntimeException {

    public MessageDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
