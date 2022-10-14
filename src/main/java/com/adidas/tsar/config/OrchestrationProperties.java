package com.adidas.tsar.config;

import com.adidas.tsar.dto.orchestration.OrchestrationStage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.orchestration.stage")
@Getter
@Setter
public class OrchestrationProperties {

    private OrchestrationStage calculate;
    private OrchestrationStage send;

}
