package com.adidas.tsar.service;

import com.adidas.pc.core.kafka.api.KafkaEventPublisher;
import com.adidas.pc.core.kafka.event.SimpleApplicationEvent;
import com.adidas.pc.core.kafka.event.SimpleEventMetadata;
import com.adidas.tsar.dto.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PublisherService {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Value("${spring.application.name}")
    private String applicationName;

    public <T> void publishEvent(KafkaEvent<T> eventDto) {
        publish(eventDto, prepareMetadata(eventDto));
    }

    @SuppressWarnings("unchecked")
    private <T, S extends SimpleEventMetadata> void publish(KafkaEvent<T> eventDto, S metadata) {
        kafkaEventPublisher.send(
            eventDto.getTopic(),
            eventDto.getId(),
            new SimpleApplicationEvent<>(metadata, eventDto.getPayload())
        );
    }

    private <T> SimpleEventMetadata prepareMetadata(KafkaEvent<T> event) {
        return new SimpleEventMetadata(
            event.getVersion(),
            applicationName,
            event.getName(),
            LocalDateTime.now()
        );
    }

}
