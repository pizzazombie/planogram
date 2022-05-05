package com.adidas.tsar.rest.feign;

import com.adidas.tsar.dto.BaseErrorResponse;
import com.adidas.tsar.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static feign.FeignException.errorStatus;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException decode = errorStatus(StringUtils.EMPTY, response);
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());

        final var errorResponse = marshalExceptionBody(decode, new TypeReference<BaseErrorResponse>() {
        });
        var apiRequestException = new ApiException(
            errorResponse.map(it -> it.getInfo().getTitle()).orElseGet(httpStatus::getReasonPhrase),
            httpStatus,
            response.request().url(),
            errorResponse.map(BaseErrorResponse::toString).orElse(StringUtils.EMPTY)
        );
        log.warn("[FEIGN ERROR INFO] Problem during request: {} [{}], status code: [{}]",
            response.request().httpMethod(), apiRequestException.getRequestUrl(), apiRequestException.getStatus());
        return apiRequestException;
    }

    private <T> Optional<T> marshalExceptionBody(FeignException decode, TypeReference<T> tClass) {
        return decode.responseBody()
            .map(StandardCharsets.UTF_8::decode)
            .map(CharBuffer::toString)
            .flatMap(body -> getObject(body, tClass));
    }

    private <T> Optional<T> getObject(String body, TypeReference<T> tClass) {
        try {
            return Optional.of(objectMapper.readValue(body, tClass));
        } catch (JsonProcessingException e) {
            log.info("[FEIGN ERROR INFO] Problem during marshalling body: {}.", body);
            return Optional.empty();
        }
    }

}