package com.adidas.tsar.config;

import com.adidas.pc.core.kafka.api.KafkaEventPublisher;
import com.adidas.pc.core.kafka.impl.KafkaEventPublisherImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {
    private String actionTopic;
    private String resultTopic;
    private String detegoTopic;
    private String messagesVersion;

    @Bean
    @Autowired
    public KafkaEventPublisher kafkaEventPublisher(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        KafkaEventPublisherImpl publisher = new KafkaEventPublisherImpl(kafkaTemplate);
        publisher.setSerializer(new JsonSerializer(objectMapper));
        return publisher;
    }
}
