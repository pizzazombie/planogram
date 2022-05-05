package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseErrorResponse;
import com.adidas.tsar.exceptions.AppException;
import com.adidas.tsar.exceptions.ImportFileValidationException;
import com.adidas.tsar.mapper.ErrorResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestControllerAdvice {

    private static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    @ExceptionHandler(ImportFileValidationException.class)
    public HttpEntity<BaseErrorResponse> handleApp(ImportFileValidationException e, final HttpServletRequest request) {
        log.error("An import process failed", e);
        var response = ErrorResponseFactory.getErrorResponseWithInnerErrors(
            e.getErrorSection(),
            e.getClass().getSimpleName(),
            e.getMessage(),
            HttpStatus.BAD_REQUEST,
            e.getValidationErrors()
        );
        return new ResponseEntity<>(response, overrideContentType(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppException.class)
    public HttpEntity<BaseErrorResponse> handleApp(AppException e, final HttpServletRequest request) {
        log.error("An application error occurred", e);
        var response = ErrorResponseFactory.getErrorResponse(e.getErrorSection(), e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, overrideContentType(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public HttpEntity<BaseErrorResponse> handle(ConstraintViolationException e, final HttpServletRequest request) {
        log.warn("Validation request error", e);
        var response = ErrorResponseFactory.getErrorResponseWithInnerErrors(
            request.getRequestURI(),
            "Bad request - validation error",
            null,
            HttpStatus.BAD_REQUEST,
            e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList())
        );
        return new ResponseEntity<>(response, overrideContentType(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<BaseErrorResponse> handle(MethodArgumentNotValidException e, final HttpServletRequest request) {
        log.warn("Validation request error", e);
        var response = ErrorResponseFactory.getErrorResponseWithInnerErrors(
            request.getRequestURI(),
            "Bad request - validation error",
            null,
            HttpStatus.BAD_REQUEST,
            e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList())
        );
        return new ResponseEntity<>(response, overrideContentType(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public HttpEntity<BaseErrorResponse> handleApp(Exception e, final HttpServletRequest request) {
        log.error("An unexpected error occurred", e);
        var response = ErrorResponseFactory.getErrorResponse("Unexpected error occurred: " + request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, overrideContentType(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpHeaders overrideContentType() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", APPLICATION_PROBLEM_JSON);
        return httpHeaders;
    }

}
