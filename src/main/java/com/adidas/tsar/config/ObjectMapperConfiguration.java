package com.adidas.tsar.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;


@Data
@Configuration
public class ObjectMapperConfiguration {

    @Value("${app.date-format}")
    private String dateFormat;
    @Value("${app.date-time-format}")
    private String dateTimeFormat;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizeJackson2ObjectMapperBuilder() {
        return builder -> builder
            .simpleDateFormat(dateTimeFormat)
            .serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)))
            .deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)))
            .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
            .deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .modules(new JavaTimeModule());
    }

}
