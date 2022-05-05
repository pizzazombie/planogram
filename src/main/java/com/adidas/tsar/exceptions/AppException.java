package com.adidas.tsar.exceptions;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Collection;

@Getter
public class AppException extends RuntimeException {

    private final String errorSection;

    @Nullable
    private final Collection<String> innerErrors;

    public AppException(String message) {
        this("Unexpected error", message);
    }

    public AppException(String errorSection, String message) {
        this(errorSection, message, null);
    }

    public AppException(String errorSection, String message, @Nullable Collection<String> innerErrors) {
        super(message);
        this.errorSection = errorSection;
        this.innerErrors = innerErrors;
    }

}
