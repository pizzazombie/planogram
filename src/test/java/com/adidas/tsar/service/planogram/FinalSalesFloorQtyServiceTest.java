package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.planogram.FinalPlanogramDecorator;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
class FinalSalesFloorQtyServiceTest extends BaseIntegrationTest {
    @Autowired
    FinalSalesFloorQtyService finalSalesFloorQtyService;

    @Test
    void populateFinalSalesFloorQty_actualRidRedIsExists_finalSalesFloorQtyIsZero_ignoreForReverseReplenishmentIsOne() {
        final int PRES_MIN = 6;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        FinalPlanogramDecorator chunk = prepareFinalPlanogramDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 2),
            Pair.of(matrix2, 3)),
            Collections.singletonList(prepareRidRed(ARTICLE, true)),
            Lists.emptyList()
        );

        finalSalesFloorQtyService.populateFinalSalesFloorQty(chunk);

        assertEquals(0, chunk.getItems().get(matrix1).getFinalSalesFloorQty());
        assertEquals(1, chunk.getItems().get(matrix1).getIgnoreForReverseReplenishment());
        assertEquals(0, chunk.getItems().get(matrix2).getFinalSalesFloorQty());
        assertEquals(1, chunk.getItems().get(matrix2).getIgnoreForReverseReplenishment());
    }

    @Test
    void populateFinalSalesFloorQty_actualRidRedIsNotExistsAndRemovalExists_FinalSalesFloorQtyIsZero_ignoreForReverseReplenishmentIsZero() {
        final int PRES_MIN = 6;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        FinalPlanogramDecorator chunk = prepareFinalPlanogramDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 2),
            Pair.of(matrix2, 3)),
            Collections.singletonList(prepareRidRed(ARTICLE, false)),
            Collections.singletonList(prepareRemoval(ARTICLE, "C42J"))
        );

        finalSalesFloorQtyService.populateFinalSalesFloorQty(chunk);

        assertEquals(0, chunk.getItems().get(matrix1).getFinalSalesFloorQty());
        assertEquals(0, chunk.getItems().get(matrix1).getIgnoreForReverseReplenishment());
        assertEquals(0, chunk.getItems().get(matrix2).getFinalSalesFloorQty());
        assertEquals(0, chunk.getItems().get(matrix2).getIgnoreForReverseReplenishment());
    }

    @Test
    void populateFinalSalesFloorQty_actualRidRedIsNotExistsAndRemovalNotExists_FinalSalesFloorQtyIsEqualsToSalesFloorQty_ignoreForReverseReplenishmentIsOne() {
        final int PRES_MIN = 6;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        FinalPlanogramDecorator chunk = prepareFinalPlanogramDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 2),
            Pair.of(matrix2, 3)),
            Collections.singletonList(prepareRidRed(ARTICLE, false)),
            Lists.emptyList()
        );

        finalSalesFloorQtyService.populateFinalSalesFloorQty(chunk);

        assertEquals(2, chunk.getItems().get(matrix1).getFinalSalesFloorQty());
        assertEquals(1, chunk.getItems().get(matrix1).getIgnoreForReverseReplenishment());
        assertEquals(3, chunk.getItems().get(matrix2).getFinalSalesFloorQty());
        assertEquals(1, chunk.getItems().get(matrix2).getIgnoreForReverseReplenishment());
    }

    private Ridred prepareRidRed(ArticleDto article, boolean isActual) {
        return new Ridred(EMPTY_ID,
            article.getId(),
            LocalDate.now().minusDays(2),
            isActual ? LocalDate.now().plusDays(1) : LocalDate.now().minusDays(1));
    }

    private Removal prepareRemoval(ArticleDto article, String storeCode) {
        return new Removal(EMPTY_ID, article.getId(), storeCode, "REMOVAL_NUMBER");
    }

    private FinalPlanogramDecorator prepareFinalPlanogramDecorator(ArticleDto article, int presMin, List<Pair<Matrix, Integer>> matrixAndSalesFloorQtyPairList, List<Ridred> ridReds, List<Removal> removals) {
        PrioritiesDecorator prioritiesDecorator = new PrioritiesDecorator(
            new MatricesByArticleImpl(article, matrixAndSalesFloorQtyPairList.stream().map(Pair::getKey).collect(Collectors.toList()))
        );
        final var salesFloorQtyDecorator = new SalesFloorQtyDecorator(prioritiesDecorator, presMin);
        matrixAndSalesFloorQtyPairList.forEach(pair -> salesFloorQtyDecorator.getSalesFloorQtyByMatrix().put(pair.getKey(), pair.getValue()));
        return new FinalPlanogramDecorator(salesFloorQtyDecorator, ridReds, removals);
    }

}