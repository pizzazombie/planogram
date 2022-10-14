package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.data.FtwPriorityRepository;
import com.adidas.tsar.data.MatrixRepository;
import com.adidas.tsar.data.TotalBuyRepository;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
class PriorityCalculationServiceTest extends BaseIntegrationTest {

    @MockBean
    TotalBuyRepository totalBuyRepository;

    @MockBean
    MatrixRepository matrixRepository;

    @Autowired
    private PriorityCalculationService priorityCalculationService;

    @MockBean
    private FtwPriorityRepository ftwPriorityRepository;

    @Test
    void populateFtwPriorities_ftwPrioritiesExists_prioritiesGotFromFtwPriorities() {
        final var dictionaries = new DictionariesCollectionUtils()
            .with(BrandDto.class, Arrays.asList(BRAND, BRAND_2), dictionaryBlankName)
            .with(RmhGenderAgeDto.class, Arrays.asList(AGE, AGE_2), dictionaryBlankName);

        final var priorities = Arrays.asList(
            TestUtils.prepareFtwPriority(1L, BRAND, AGE, SIZE_INDEX_1, 1),
            TestUtils.prepareFtwPriority(2L, BRAND, AGE, SIZE_INDEX_2, 2)
        );
        when(ftwPriorityRepository.findAllByKeys(any())).thenReturn(priorities);
        var calcBatch = preparePriorityCalcBatch(ARTICLE, List.of(
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_1, 0),
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_2, 0)
        ));

        priorityCalculationService.populateFtwPriorities(dictionaries, calcBatch);

        assertEquals(1, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_1));
        assertEquals(2, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_2));
    }

    @Test
    void populateFtwPriorities_ftwPrioritiesNotExists_prioritiesPopulateByDefaultMethod() {
        final var dictionaries = new DictionariesCollectionUtils()
            .with(BrandDto.class, Arrays.asList(BRAND, BRAND_2), dictionaryBlankName)
            .with(RmhGenderAgeDto.class, Arrays.asList(AGE, AGE_2), dictionaryBlankName);
        when(ftwPriorityRepository.findAllByKeys(any())).thenReturn(Lists.emptyList());
        var calcBatch = preparePriorityCalcBatch(ARTICLE, List.of(
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_1, 0),
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_2, 0)
        ));

        priorityCalculationService.populateFtwPriorities(dictionaries, calcBatch);

        assertEquals(1, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_1));
        assertEquals(2, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_2));
    }

    @Test
    void populateFtwPriorities_notAllFtwPrioritiesExists_prioritiesGotFromFtwPrioritiesAndOtherPopulateByDefaultMethod() {
        final var dictionaries = new DictionariesCollectionUtils()
            .with(BrandDto.class, Arrays.asList(BRAND, BRAND_2), dictionaryBlankName)
            .with(RmhGenderAgeDto.class, Arrays.asList(AGE, AGE_2), dictionaryBlankName);
        final var priorities = Collections.singletonList(
            TestUtils.prepareFtwPriority(2L, BRAND, AGE, SIZE_INDEX_2, 2)
        );
        when(ftwPriorityRepository.findAllByKeys(any())).thenReturn(priorities);
        var calcBatch = preparePriorityCalcBatch(ARTICLE, List.of(
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_1, 0),
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_2, 0)
        ));

        priorityCalculationService.populateFtwPriorities(dictionaries, calcBatch);

        assertEquals(3, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_1));
        assertEquals(2, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_2));
    }

    @Test
    void populateAppPriorities_totalBuyExists_prioritiesGotFromTotalBuys() {
        final var totalBuys = Arrays.asList(
            TestUtils.prepareTotalBuy(ARTICLE, SIZE_INDEX_2, 10),
            TestUtils.prepareTotalBuy(ARTICLE, SIZE_INDEX_1, 20)
        );
        when(totalBuyRepository.findTotalBuyByArticleIdOrderByQuantityDescSizeIndexAsc(eq(ARTICLE.getId()))).thenReturn(totalBuys);
        when(matrixRepository.findByArticleIdAndSizeIndexIn(eq(ARTICLE.getId().longValue()), any())).thenReturn(Lists.emptyList());
        var calcBatch = preparePriorityCalcBatch(ARTICLE, List.of(
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_1, 0),
            TestUtils.prepareMatrix(ARTICLE, STORE_1.getId(), SIZE_INDEX_2, 0)
        ));

        priorityCalculationService.populateAppPriorities(calcBatch);

        assertEquals(1, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_2));
        assertEquals(2, calcBatch.getPrioritiesBySizeIndex().get(SIZE_INDEX_1));
    }

    private PrioritiesDecorator preparePriorityCalcBatch(ArticleDto article, List<Matrix> matrices) {
        MatricesByArticleImpl matricesByArticle = new MatricesByArticleImpl(article, matrices);
        return new PrioritiesDecorator(matricesByArticle);
    }

}