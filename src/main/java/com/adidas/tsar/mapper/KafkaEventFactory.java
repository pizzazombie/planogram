package com.adidas.tsar.mapper;

import com.adidas.tsar.config.KafkaProperties;
import com.adidas.tsar.dto.KafkaEvent;
import com.adidas.tsar.dto.orchestration.CommandActionDto;
import com.adidas.tsar.dto.orchestration.CommandResultDto;
import com.adidas.tsar.dto.orchestration.OrchestrationStage;
import com.adidas.tsar.dto.planogram.PlanogramByStoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class KafkaEventFactory {

    private final KafkaProperties kafkaProperties;

    public KafkaEvent<CommandResultDto> getCommandResultEvent(OrchestrationStage stage) {
        return new KafkaEvent<>(
            kafkaProperties.getResultTopic(),
            UUID.randomUUID().toString(),
            kafkaProperties.getMessagesVersion(),
            stage.getName(),
            new CommandResultDto(stage.getName(), stage.getCommand())
        );
    }

    public KafkaEvent<CommandActionDto<PlanogramByStoreDto>> getTransferPlanogramEvent(String name, String command, PlanogramByStoreDto payload) {
        return new KafkaEvent<>(
            kafkaProperties.getDetegoTopic(),
            UUID.randomUUID().toString(),
            kafkaProperties.getMessagesVersion(),
            name,
            new CommandActionDto<>(name, command, payload)
        );
    }

}
