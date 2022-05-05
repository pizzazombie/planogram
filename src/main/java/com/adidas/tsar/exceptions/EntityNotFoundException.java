package com.adidas.tsar.exceptions;

public class EntityNotFoundException extends AppException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String errorSection, String message) {
        super(errorSection, message);
    }
}
