package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.config.OrchestrationProperties;
import com.adidas.tsar.data.PlanogramDao;
import com.adidas.tsar.data.PlanogramRepository;
import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.KafkaEvent;
import com.adidas.tsar.dto.StoreDto;
import com.adidas.tsar.dto.orchestration.CommandActionDto;
import com.adidas.tsar.dto.planogram.PlanogramByStoreDto;
import com.adidas.tsar.service.PublisherService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.liquibase.enabled=false"})
class PlanogramTransferServiceTest extends BaseIntegrationTest {

    @Autowired
    private PlanogramTransferService planogramTransferService;
    @Autowired
    private OrchestrationProperties orchestrationProperties;

    @MockBean
    private PlanogramDao planogramDao;
    @MockBean
    private PlanogramRepository planogramRepository;
    @MockBean
    private PublisherService publisherService;
    @Captor
    private ArgumentCaptor<KafkaEvent<CommandActionDto<PlanogramByStoreDto>>> kafkaCommandActionCaptor;

    @Test
    void sendPlanograms_transferFinishEventSent() {
        final boolean IGNORE_FOR_REVERSE_REPLENISHMENT = false;
        final var planogram1 = preparePlanogram(ARTICLE, STORE_1, SKU_1, 0, 0, 0, 0, IGNORE_FOR_REVERSE_REPLENISHMENT, LocalDateTime.now());
        final var planogram2 = preparePlanogram(ARTICLE, STORE_1, SKU_2, 0, 0, 0, 0, IGNORE_FOR_REVERSE_REPLENISHMENT, LocalDateTime.now());
        when(planogramDao.findAllStoreCodes()).thenReturn(Set.of(STORE_1.getSap()));
        when(planogramRepository.findAllByStoreCode(anyString())).thenReturn(Arrays.asList(planogram1, planogram2));

        planogramTransferService.sendPlanograms();

        verify(publisherService, atLeastOnce()).publishEvent(kafkaCommandActionCaptor.capture());
        final var commandAction = kafkaCommandActionCaptor.getAllValues().get(0).getPayload();

        final var planogramByStoreDto = commandAction.getPayload();
        assertEquals(STORE_1.getSap(), planogramByStoreDto.getStoreCode());
        assertEquals(1, planogramByStoreDto.getArticles().size());

        final var articlePlanograms = planogramByStoreDto.getArticles().get(0);
        assertEquals(ARTICLE.getCode(), articlePlanograms.getArticleCode());
        assertEquals(IGNORE_FOR_REVERSE_REPLENISHMENT, articlePlanograms.isIgnoreForReverseReplenishment());
        assertEquals(2, articlePlanograms.getProductPlanograms().size());
        assertPlanogramProduct(articlePlanograms.getProductPlanograms().get(0), planogram1);
        assertPlanogramProduct(articlePlanograms.getProductPlanograms().get(1), planogram2);

    }

    private void assertPlanogramProduct(PlanogramByStoreDto.ProductPlanogram productPlanogram, Planogram planogram) {
        assertEquals(planogram.getGtin(), productPlanogram.getGtin());
        assertEquals(planogram.getPriority(), productPlanogram.getPriority());
        assertEquals(planogram.getFinalSalesFloorQty(), productPlanogram.getFinalSalesFloorQty());
    }

    private Planogram preparePlanogram(ArticleDto article, StoreDto store, ArticleDto.SkuResponseDto sku, int priority, int presMin, int salesFloorQty, int finalSalesFloorQty, boolean ignoreForReverseReplenishment, LocalDateTime calculatedAt) {
        return new Planogram(
            0L,
            article.getCode(),
            store.getSap(),
            sku.getGtin(),
            sku.getSizeIndex(),
            priority,
            presMin,
            salesFloorQty,
            finalSalesFloorQty,
            ignoreForReverseReplenishment,
            calculatedAt
        );
    }
}