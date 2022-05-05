package com.adidas.tsar.mapper;

import com.adidas.tsar.dto.BaseErrorResponse;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ErrorResponseFactory {

    public BaseErrorResponse getErrorResponse(String title, String message, HttpStatus httpStatus) {
        return getErrorResponse(title, message, null, httpStatus, null);
    }

    public BaseErrorResponse getErrorResponse(String title, String message, String detail, HttpStatus httpStatus) {
        return getErrorResponse(title, message, detail, httpStatus, null);
    }

    public BaseErrorResponse getErrorResponseWithInnerErrors(String title, String message, String detail, HttpStatus httpStatus, Collection<String> innerErrors) {
        return getErrorResponse(
            title,
            message,
            detail,
            httpStatus,
            innerErrors.stream().map(it -> Pair.<String, String>of(it, null)).collect(Collectors.toList())
        );
    }

    public BaseErrorResponse getErrorResponse(String title,
                                              String message,
                                              @Nullable String detail,
                                              HttpStatus httpStatus,
                                              @Nullable Collection<Pair<String, String>> innerErrors) {
        return new BaseErrorResponse(
            new BaseErrorResponse.Info(
                title,
                new BaseErrorResponse.Error(
                    message,
                    httpStatus.value(),
                    detail,
                    Optional.ofNullable(innerErrors)
                        .map(it -> it.stream()
                            .map(pair -> new BaseErrorResponse.InnerError(pair.getLeft(), pair.getRight()))
                            .collect(Collectors.toList()))
                        .orElse(null)
                )
            )
        );
    }

}