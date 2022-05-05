package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
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
class SalesFloorQtyCalculationServiceTest extends BaseIntegrationTest {

    @Autowired
    SalesFloorQtyCalculationService salesFloorQtyCalculationService;

    @Test
    void populateSalesFloorQty_ftwLogic_oneForOnlyFirstMatrixWithMaxPriority() {
        final int FTW_PRES_MIN = 1;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "600", 1);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "610", 1);
        Matrix matrix3 = prepareMatrix(3, ARTICLE.getId(), "C42J", "630", 2);
        Matrix matrix4 = prepareMatrix(4, ARTICLE.getId(), "C42J", "640", 1);
        Matrix matrix5 = prepareMatrix(5, ARTICLE.getId(), "C42J", "650", 2);
        SalesFloorQtyDecorator chunk = prepareSalesFloorQtyDecorator(ARTICLE, FTW_PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 5),
            Pair.of(matrix2, 2),
            Pair.of(matrix3, 3),
            Pair.of(matrix4, 4),
            Pair.of(matrix5, 6)
        ));

        salesFloorQtyCalculationService.populateSalesFloorQty(chunk);

        assertEquals(0, chunk.getSalesFloorQtyByMatrix().get(matrix1));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix2));
        assertEquals(0, chunk.getSalesFloorQtyByMatrix().get(matrix3));
        assertEquals(0, chunk.getSalesFloorQtyByMatrix().get(matrix4));
        assertEquals(0, chunk.getSalesFloorQtyByMatrix().get(matrix5));
    }

    @Test
    void populateAppSalesFloorQty_presMinAboveThanMatrixCountInChunk_matrixByPriorityHaveAdditionalSalesFloorQty() {
        final int PRES_MIN = 6;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 1);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        Matrix matrix3 = prepareMatrix(3, ARTICLE.getId(), "C42J", "230", 1);
        Matrix matrix4 = prepareMatrix(4, ARTICLE.getId(), "C42J", "250", 1);
        SalesFloorQtyDecorator chunk = prepareSalesFloorQtyDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 6),
            Pair.of(matrix2, 1),
            Pair.of(matrix3, 2),
            Pair.of(matrix4, 3)
        ));

        salesFloorQtyCalculationService.populateSalesFloorQty(chunk);

        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix1));
        assertEquals(2, chunk.getSalesFloorQtyByMatrix().get(matrix2));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix3));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix4));
    }

    @Test
    void populateAppSalesFloorQty_presMinLessThanMatrixCountInChunk_notMoreThanPresMinCountOfMatrixHaveSalesFloorQty() {
        final int PRES_MIN = 6;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        Matrix matrix3 = prepareMatrix(3, ARTICLE.getId(), "C42J", "230", 1);
        Matrix matrix4 = prepareMatrix(4, ARTICLE.getId(), "C42J", "250", 4);
        Matrix matrix5 = prepareMatrix(5, ARTICLE.getId(), "C42J", "270", 2);
        Matrix matrix6 = prepareMatrix(6, ARTICLE.getId(), "C42J", "290", 1);
        Matrix matrix7 = prepareMatrix(7, ARTICLE.getId(), "C42J", "310", 2);
        SalesFloorQtyDecorator chunk = prepareSalesFloorQtyDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 6),
            Pair.of(matrix2, 1),
            Pair.of(matrix3, 2),
            Pair.of(matrix4, 3),
            Pair.of(matrix5, 4),
            Pair.of(matrix6, 5),
            Pair.of(matrix7, 7)
        ));

        salesFloorQtyCalculationService.populateSalesFloorQty(chunk);

        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix1));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix2));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix3));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix4));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix5));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix6));
        assertEquals(0, chunk.getSalesFloorQtyByMatrix().get(matrix7));
    }

    @Test
    void populateAppSalesFloorQty_presMinMoreAboveThanMatrixCountInChunk_salesFloorQtyIsLessOrEqualsThanMatrixQuantity() {
        final int PRES_MIN = 10;
        Matrix matrix1 = prepareMatrix(1, ARTICLE.getId(), "C42J", "190", 3);
        Matrix matrix2 = prepareMatrix(2, ARTICLE.getId(), "C42J", "210", 2);
        Matrix matrix3 = prepareMatrix(3, ARTICLE.getId(), "C42J", "230", 1);
        Matrix matrix4 = prepareMatrix(4, ARTICLE.getId(), "C42J", "250", 1);
        SalesFloorQtyDecorator chunk = prepareSalesFloorQtyDecorator(ARTICLE, PRES_MIN, Arrays.asList(
            Pair.of(matrix1, 6),
            Pair.of(matrix2, 1),
            Pair.of(matrix3, 2),
            Pair.of(matrix4, 3)
        ));

        salesFloorQtyCalculationService.populateSalesFloorQty(chunk);

        assertEquals(3, chunk.getSalesFloorQtyByMatrix().get(matrix1));
        assertEquals(2, chunk.getSalesFloorQtyByMatrix().get(matrix2));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix3));
        assertEquals(1, chunk.getSalesFloorQtyByMatrix().get(matrix4));
    }

    private SalesFloorQtyDecorator prepareSalesFloorQtyDecorator(ArticleDto article, int presMin, List<Pair<Matrix, Integer>> matrixAndPriorityPairList) {
        PrioritiesDecorator prioritiesDecorator = new PrioritiesDecorator(
            new MatricesByArticleImpl(article, matrixAndPriorityPairList.stream().map(Pair::getKey).collect(Collectors.toList()))
        );
        matrixAndPriorityPairList
            .forEach(pair -> prioritiesDecorator.setPriority(pair.getKey().getSizeIndex(), pair.getValue()));
        return new SalesFloorQtyDecorator(prioritiesDecorator, presMin);
    }
}