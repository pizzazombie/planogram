package com.adidas.tsar.exceptions;

import lombok.Getter;

import java.util.Collection;

@Getter
public class ImportFileValidationException extends ImportException {
    private final Collection<String> validationErrors;

    public ImportFileValidationException(String message, Collection<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
}
