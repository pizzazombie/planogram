package com.adidas.tsar;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.listener.CommandActionListener;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseIntegrationTest {

    protected static final long EMPTY_ID = 0L;
    protected static final int PAGE_SIZE = 10;
    protected static final String SIZE_INDEX_1 = "001";
    protected static final String SIZE_INDEX_2 = "002";
    protected static final SizeScaleDto EMPTY_SIZE_SCALE = new SizeScaleDto(-1, "-");
    protected static final SizeScaleDto SIZE_SCALE = new SizeScaleDto(1, "RT");
    protected static final SizeScaleDto SIZE_SCALE_2 = new SizeScaleDto(2, "RR");
    protected static final String REMOVAL_NUM_1 = "202150C0TS_4";
    protected static final String REMOVAL_NUM_2 = "20227C047_6";
    protected static final LocalDate RID_1 = LocalDate.of(2018, 11, 27);
    protected static final LocalDate RID_2 = LocalDate.of(2018, 05, 13);
    protected static final LocalDate RED_1 = LocalDate.of(2021, 12, 30);
    protected static final LocalDate RED_2 = LocalDate.of(2022, 04, 11);
    protected static final String USER = "tsar-api";
    protected static final String SAP_1 = "C22C";
    protected static final String SAP_2 = "C42Y";
    protected static final StoreDto STORE_1 = new StoreDto(1, "store1", SAP_1);
    protected static final StoreDto STORE_2 = new StoreDto(2, "store2", SAP_2);
    protected final BrandDto EMPTY_BRAND = new BrandDto(-1, "-");
    protected final BrandDto BRAND = new BrandDto(1, "ADIDAS");
    protected final BrandDto BRAND_2 = new BrandDto(2, "REEBOK");
    protected final RmhGenderAgeDto EMPTY_AGE = new RmhGenderAgeDto(-1, "-");
    protected final RmhGenderAgeDto AGE = new RmhGenderAgeDto(1, "MEN");
    protected final RmhGenderAgeDto AGE_2 = new RmhGenderAgeDto(2, "WOMEN");
    protected final RmhCategoryDto EMPTY_CATEGORY = new RmhCategoryDto(-1, "-");
    protected final RmhCategoryDto CATEGORY = new RmhCategoryDto(1, "ACT");
    protected final RmhCategoryDto CATEGORY_2 = new RmhCategoryDto(2, "TRAINING");
    protected final RmhProductTypeDto PRODUCT_TYPE = new RmhProductTypeDto(1, "ANKLE SOCKS");
    protected final RmhProductTypeDto PRODUCT_TYPE_2 = new RmhProductTypeDto(2, "CAP");
    protected final RmhProductDivisionDto EMPTY_DIVISION = new RmhProductDivisionDto(-1, "-");
    protected final RmhProductDivisionDto DIVISION = new RmhProductDivisionDto(1, "ACC/HW");
    protected final RmhProductDivisionDto DIVISION_2 = new RmhProductDivisionDto(2, "APPAREL");
    protected final RmhProductDivisionDto DIVISION_FOOTWEAR = new RmhProductDivisionDto(3, "FOOTWEAR");
    protected final ArticleDto.SkuResponseDto SKU_1 = new ArticleDto.SkuResponseDto(1, "0000000000000", "AA", SIZE_INDEX_1);
    protected final ArticleDto.SkuResponseDto SKU_2 = new ArticleDto.SkuResponseDto(1, "0000000000001", "AA", SIZE_INDEX_2);
    protected final ArticleDto ARTICLE = TestUtils.prepareArticle(1L, "articleName1", "BA9281", BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, List.of(SKU_1, SKU_2));
    protected final ArticleDto ARTICLE_2 = TestUtils.prepareArticle(2L, "articleName2", "BA9282", BRAND_2, AGE_2, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_2, SIZE_SCALE_2, List.of(SKU_1, SKU_2));

    @MockBean
    protected TsarMasterDataApiClient tsarMasterDataApiClient;

    @MockBean
    protected CommandActionListener commandActionListener;

    @Captor
    protected ArgumentCaptor<List<Long>> longListCaptor;

    @Value("${app.dictionary-blank-name}")
    protected String dictionaryBlankName;

    @BeforeEach
    void setup() {
        when(tsarMasterDataApiClient.getBrands()).thenReturn(buildArrayBaseResponse(List.of(EMPTY_BRAND, BRAND, BRAND_2)));
        when(tsarMasterDataApiClient.getRmhGenderAges()).thenReturn(buildArrayBaseResponse(List.of(EMPTY_AGE, AGE, AGE_2)));
        when(tsarMasterDataApiClient.getRmhCategories()).thenReturn(buildArrayBaseResponse(List.of(EMPTY_CATEGORY, CATEGORY, CATEGORY_2)));
        when(tsarMasterDataApiClient.getRmhProductTypes()).thenReturn(buildArrayBaseResponse(List.of(PRODUCT_TYPE, PRODUCT_TYPE_2)));
        when(tsarMasterDataApiClient.getRmhProductDivisions()).thenReturn(buildArrayBaseResponse(List.of(EMPTY_DIVISION, DIVISION, DIVISION_2, DIVISION_FOOTWEAR)));
        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));
        when(tsarMasterDataApiClient.getSizeScales()).thenReturn(buildArrayBaseResponse(List.of(EMPTY_SIZE_SCALE, SIZE_SCALE, SIZE_SCALE_2)));
        when(tsarMasterDataApiClient.getStores()).thenReturn(buildArrayBaseResponse(List.of(STORE_1, STORE_2)));
    }

    protected <T> BaseResponse<List<T>> buildArrayBaseResponse(List<T> data) {
        return new BaseResponse<>(
            new BaseResponse.Info("The section title", null),
            new BaseResponse.Params(new BaseResponse.Page(null, null, null, data.size())),
            data
        );
    }

    protected Matrix prepareMatrix(int id, long articleId, int storeId, String sizeIndex, int quantity) {
        return new Matrix(id, articleId, sizeIndex, quantity, storeId);
    }

}
