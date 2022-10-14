package com.adidas.tsar.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("app.filter.article")
@Getter
public class FilterProperties {
    private final Map<String, String> fieldsMapping = new HashMap<>();
}

