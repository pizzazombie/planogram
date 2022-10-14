package com.adidas.tsar.listener;

import com.adidas.pc.core.kafka.event.SimpleApplicationEvent;
import com.adidas.tsar.config.OrchestrationProperties;
import com.adidas.tsar.dto.orchestration.CommandActionDto;
import com.adidas.tsar.dto.orchestration.OrchestrationStage;
import com.adidas.tsar.service.UnmarshallerService;
import com.adidas.tsar.service.planogram.PlanogramCalculationService;
import com.adidas.tsar.service.planogram.PlanogramTransferService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CommandActionListener {
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final OrchestrationProperties orchestrationProperties;
    private final PlanogramCalculationService planogramCalculationService;
    private final PlanogramTransferService planogramTransferService;
    private final UnmarshallerService unmarshallerService;
    private AsyncListenableTaskExecutor executor;

    @Autowired
    @Qualifier("asyncSingleThreadTaskExecutor")
    public void setExecutor(AsyncListenableTaskExecutor executor) {
        this.executor = executor;
    }

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${app.kafka.action-topic}")
    public void onMessage(@Payload String message,
                          @Headers KafkaMessageHeaders headers,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          Acknowledgment acknowledgment) {
        log.info("Received an event from kafka, topic: {}, headers: {}, message: {}", topic, headers, message);
        try {
            final SimpleApplicationEvent<?> actionEvent = unmarshallerService.unmarshal(message, new TypeReference<>() {
            });
            final CommandActionDto<?> actionDto = unmarshallerService.convertObject(actionEvent.getPayload(), CommandActionDto.class);
            final OrchestrationStage stage = new OrchestrationStage(actionDto.getService(), actionDto.getCommand());
            if (stage.equals(orchestrationProperties.getCalculate())) {
                pauseConsumerAndRunAsync(this::processCalculateEvent, acknowledgment);
            } else if (stage.equals(orchestrationProperties.getSend())) {
                pauseConsumerAndRunAsync(this::processSendEvent, acknowledgment);
            } else {
                log.info("Received event was skipped, because has a different stage, message: {}", message);
            }
            log.info("Received Send event was successfully processed, message: {}", message);
        } catch (Exception e) {
            log.error("Kafka consumer stopped consuming messages, according to errors." +
                    " Please fix the bug or skip message offset: {}, partition: {}, key: {}, message: {}",
                headers.get(KafkaHeaders.OFFSET), headers.get(KafkaHeaders.PARTITION_ID),
                headers.get(KafkaHeaders.MESSAGE_KEY), message, e);
        }

    }

    private void pauseConsumerAndRunAsync(Runnable handler, Acknowledgment acknowledgment) {
        pauseConsumer(consumerGroupId);
        executor.submitListenable(handler)
            .addCallback(result -> {
                acknowledgment.acknowledge();
                resumeConsumer(consumerGroupId);
            }, ex -> {
                log.error("Error happened during handle kafka event", ex);
                acknowledgment.acknowledge();
                resumeConsumer(consumerGroupId);
            });
    }

    private void processCalculateEvent() {
        planogramCalculationService.calculatePlanogram();
    }

    private void processSendEvent() {
        planogramTransferService.sendPlanograms();
    }

    private void pauseConsumer(String listenerId) {
        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(listenerId)).ifPresent(messageListenerContainer -> {
            log.info("Listener {} is paused", listenerId);
            messageListenerContainer.pause();
        });
    }

    private void resumeConsumer(String listenerId) {
        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(listenerId)).ifPresent(messageListenerContainer -> {
            messageListenerContainer.resume();
            log.info("Listener {} is resumed", listenerId);
        });
    }
}
