package com.adidas.tsar.service.planogram;

import com.adidas.tsar.config.OrchestrationProperties;
import com.adidas.tsar.data.PlanogramDao;
import com.adidas.tsar.data.PlanogramRepository;
import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.mapper.KafkaEventFactory;
import com.adidas.tsar.mapper.PlanogramMapper;
import com.adidas.tsar.service.PublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramTransferService {

    private final PublisherService publisherService;
    private final PlanogramRepository planogramRepository;
    private final PlanogramDao planogramDao;
    private final PlanogramMapper planogramMapper;
    private final KafkaEventFactory kafkaEventFactory;
    private final OrchestrationProperties orchestrationProperties;

    public void sendPlanograms() {
        log.info("Start sending planogram results");
        var storeCodes = planogramDao.findAllStoreCodes();
        storeCodes.forEach(storeCode -> {
            List<Planogram> storePlanograms = planogramRepository.findAllByStoreCode(storeCode);
            sendPlanogramBatch(storeCode, storePlanograms);
        });
        sendPlanogramSendFinishEvent();
        log.info("Finish sending planogram results");
    }

    public void sendPlanogramBatch(String storeCode, List<Planogram> planograms) {
        log.info("Publish batch of planogram results, storeCode {}", storeCode);
        final var planogramsDto = planogramMapper.toPlanogramByStoreDto(storeCode, planograms);
        final var event = kafkaEventFactory.getTransferPlanogramEvent("tsar.detego", "send", planogramsDto);
        publisherService.publishEvent(event);
    }

    private void sendPlanogramSendFinishEvent() {
        log.info("Publishing planogram send finish");
        final var event = kafkaEventFactory.getCommandResultEvent(orchestrationProperties.getSend());
        publisherService.publishEvent(event);
    }

}
