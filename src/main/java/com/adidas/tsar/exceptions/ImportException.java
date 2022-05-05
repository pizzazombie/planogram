package com.adidas.tsar.exceptions;

public class ImportException extends AppException {
    public ImportException(String message) {
        super("FtwPriority import", message);
    }
}
