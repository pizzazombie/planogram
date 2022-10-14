package com.adidas.tsar.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class KafkaEvent<T> {
    private final String topic;

    private final String id;
    private final String version;
    private final String name;
    private final T payload;
}
