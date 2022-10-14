package com.adidas.tsar.service;

import com.adidas.tsar.exceptions.MessageDeserializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Unmarshall messages from Kafka topics into classes or Parametrized classes
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UnmarshallerService {

    private final ObjectMapper objectMapper;

    /**
     * Convert object to POJO instance.
     *
     * @param value value to convert.
     * @param clazz class of returned instance.
     * @param <T>   type of returned instance.
     * @return instance of pojo object.
     */
    public <T> T convertObject(Object value, Class<T> clazz) {
        return objectMapper.convertValue(value, clazz);
    }


    /**
     * @param str     plain text message to be deserialized
     * @param typeRef target deserialization result class type reference
     * @param <T>     type of target deserialization class
     * @return instance of class {@code T} deserialized from {@code str} message
     * @throws MessageDeserializationException in case of any deserialization issue
     */
    public <T> T unmarshal(String str, TypeReference<T> typeRef) throws MessageDeserializationException {
        try {
            return objectMapper.readValue(str, typeRef);
        } catch (IOException e) {
            log.error("Failed to deserialize incoming message: {}", str);
            throw new MessageDeserializationException("Failed to deserialize incoming message", e);
        }
    }


    /**
     * Convert object to POJO instance.
     *
     * @param value value to convert.
     * @return json string of value.
     */
    public String convertToStringJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize incoming message {}. Details", value, e);
            throw new MessageDeserializationException("Failed to deserialize incoming message", e);
        }
    }

}
