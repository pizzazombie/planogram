package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.config.OrchestrationProperties;
import com.adidas.tsar.data.*;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.KafkaEvent;
import com.adidas.tsar.dto.orchestration.CommandResultDto;
import com.adidas.tsar.service.PublisherService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.liquibase.enabled=false"})
class PlanogramCalculationServiceTest extends BaseIntegrationTest {

    @Autowired
    private PlanogramCalculationService planogramCalculationService;
    @Autowired
    private OrchestrationProperties orchestrationProperties;
    @MockBean
    private PlanogramDao planogramDao;
    @MockBean
    private PlanogramRepository planogramRepository;
    @MockBean
    private MatrixDao matrixDao;
    @MockBean
    private MatrixRepository matrixRepository;
    @MockBean
    private RemovalRepository removalRepository;
    @MockBean
    private RidredRepository ridredRepository;
    @MockBean
    private PriorityCalculationService priorityCalculationService;
    @MockBean
    private PresMinCalculationService presMinCalculationService;
    @MockBean
    private SalesFloorQtyCalculationService salesFloorQtyCalculationService;
    @MockBean
    private FinalSalesFloorQtyService finalSalesFloorQtyService;
    @MockBean
    private PublisherService publisherService;
    @Captor
    private ArgumentCaptor<KafkaEvent<CommandResultDto>> kafkaEventCaptor;

    @Test
    void calculatePlanogram_matrixExists_planogramCalculateFinishEventSent() {
        when(ridredRepository.findAll()).thenReturn(Lists.emptyList());
        when(removalRepository.findAll()).thenReturn(Lists.emptyList());
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), STORE_1.getId(), "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE_2.getId(), STORE_2.getId(), "190", 3);
        when(matrixDao.findAll()).thenReturn(Arrays.asList(matrix1, matrix2));

        planogramCalculationService.calculatePlanogram();

        verify(planogramDao, atLeastOnce()).saveAll(anyList());
        verify(publisherService, only()).publishEvent(kafkaEventCaptor.capture());
        final var eventPayload = kafkaEventCaptor.getValue().getPayload();
        assertEquals(orchestrationProperties.getCalculate().getName(), eventPayload.getService());
    }
}