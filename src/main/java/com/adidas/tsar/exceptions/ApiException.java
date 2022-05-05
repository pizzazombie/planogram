package com.adidas.tsar.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends AppException {

    private final HttpStatus status;
    private final String requestUrl;
    private final String details;

    public ApiException(String section, HttpStatus status, String requestUrl, String details) {
        super(section, String.format("Problem during request: [%s], status code: [%s], details: %s", requestUrl, status, details));
        this.status = status;
        this.requestUrl = requestUrl;
        this.details = details;
    }

}
