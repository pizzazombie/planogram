package com.adidas.tsar.service.planogram;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.data.VmStandardRepository;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
class PresMinCalculationServiceTest extends BaseIntegrationTest {

    private final ArticleDto FOOTWEAR_ARTICLE = TestUtils.prepareArticle(1, "articleName1", "BA9281", BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION_FOOTWEAR, SIZE_SCALE, List.of(SKU_1, SKU_2));

    @MockBean
    VmStandardRepository standardRepository;

    @Autowired
    PresMinCalculationService presMinCalculationService;

    @Test
    void preparePresMinMap_standardMapBuildCorrect() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, BRAND_2, AGE_2, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_2, SIZE_SCALE_2, 2)
        );
        when(standardRepository.findAll()).thenReturn(standards);

        final var presMinMap = presMinCalculationService.preparePresMinMap();

        final var entries = presMinMap.entrySet().stream()
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .collect(Collectors.toList());

        assertEquals(standards.get(0).buildKey(), entries.get(0).getKey());
        assertEquals(standards.get(0).getPresMin(), entries.get(0).getValue());
        assertEquals(standards.get(1).buildKey(), entries.get(1).getKey());
        assertEquals(standards.get(1).getPresMin(), entries.get(1).getValue());
    }

    @Test
    void populatePresMin_footwearArticle_PresMinIsOne() {
        final var chunk = new MatricesByArticleImpl(FOOTWEAR_ARTICLE, Collections.emptyList());
        final var spyPresMinService = spy(presMinCalculationService);

        final var presMin = spyPresMinService.getPresMin(new HashMap<>(), new DictionariesCollectionUtils(Collections.emptyList()), chunk);

        assertEquals(1, presMin);
        verify(spyPresMinService, never()).getApparelPresMin(any(), any(), any(ArticleDto.class));
    }

    @Test
    void getApparelPresMin_standardExists_PresMinGotFromStandard() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, BRAND_2, AGE_2, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_2, SIZE_SCALE_2, 2)
        );
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(1, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(2, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    @Test
    void getPresMin_standardBySizeScaleNotExists_PresMinGotFromCategoryStandard() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, EMPTY_SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, BRAND_2, AGE_2, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_2, EMPTY_SIZE_SCALE, 2)

        );
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(1, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(2, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    @Test
    void getPresMin_standardByCategoryNotExists_PresMinGotFromAgeStandard() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, BRAND, AGE, EMPTY_CATEGORY, PRODUCT_TYPE, DIVISION, EMPTY_SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, BRAND_2, AGE_2, EMPTY_CATEGORY, PRODUCT_TYPE_2, DIVISION_2, EMPTY_SIZE_SCALE, 2)

        );
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(1, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(2, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    @Test
    void getPresMin_standardByAgeNotExists_PresMinGotFromBrandStandard() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, BRAND, EMPTY_AGE, EMPTY_CATEGORY, PRODUCT_TYPE, DIVISION, EMPTY_SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, BRAND_2, EMPTY_AGE, EMPTY_CATEGORY, PRODUCT_TYPE_2, DIVISION_2, EMPTY_SIZE_SCALE, 2)

        );
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(1, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(2, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    @Test
    void getPresMin_standardByBrandNotExists_PresMinGotFromProductTypeStandard() {
        List<VmStandard> standards = List.of(
            TestUtils.prepareStandard(0, EMPTY_BRAND, EMPTY_AGE, EMPTY_CATEGORY, PRODUCT_TYPE, DIVISION, EMPTY_SIZE_SCALE, 1),
            TestUtils.prepareStandard(0, EMPTY_BRAND, EMPTY_AGE, EMPTY_CATEGORY, PRODUCT_TYPE_2, DIVISION_2, EMPTY_SIZE_SCALE, 2)

        );
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(1, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(2, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    @Test
    void getPresMin_standardNotFound_PresMinIsZero() {
        List<VmStandard> standards = List.of();
        when(standardRepository.findAll()).thenReturn(standards);
        final var standardMap = presMinCalculationService.preparePresMinMap();
        final var dictionaries = prepareDictionaries();

        assertEquals(0, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE));
        assertEquals(0, presMinCalculationService.getApparelPresMin(standardMap, dictionaries, ARTICLE_2));
    }

    private DictionariesCollectionUtils prepareDictionaries() {
        return new DictionariesCollectionUtils(List.of(
            Pair.of(BrandDto.class, Arrays.asList(EMPTY_BRAND, BRAND, BRAND_2)),
            Pair.of(RmhGenderAgeDto.class, Arrays.asList(EMPTY_AGE, AGE, AGE_2)),
            Pair.of(RmhCategoryDto.class, Arrays.asList(EMPTY_CATEGORY, CATEGORY, CATEGORY_2)),
            Pair.of(RmhProductTypeDto.class, Arrays.asList(PRODUCT_TYPE, PRODUCT_TYPE_2)),
            Pair.of(RmhProductDivisionDto.class, Arrays.asList(EMPTY_DIVISION, DIVISION, DIVISION_2, DIVISION_FOOTWEAR)),
            Pair.of(SizeScaleDto.class, Arrays.asList(SIZE_SCALE, SIZE_SCALE_2))
        ));
    }

}