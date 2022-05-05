package com.adidas.tsar;

import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.*;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TestUtils {
    public final String USER = "tsar-api";
    private final String STORE_SUP_NUMBER = "StoreSup";

    public VmStandard prepareStandard(int id,
                                      BrandDto brand,
                                      RmhGenderAgeDto rmhGenderAge,
                                      RmhCategoryDto category,
                                      RmhProductTypeDto productType,
                                      RmhProductDivisionDto productDivision,
                                      SizeScaleDto sizeScale,
                                      Integer presMin) {
        return new VmStandard()
            .setId(id)
            .setBrandId(brand.getId())
            .setRmhGenderAgeId(rmhGenderAge != null ? rmhGenderAge.getId() : null)
            .setRmhCategoryId(category != null ? category.getId() : null)
            .setRmhProductTypeId(productType.getId())
            .setRmhProductDivisionId(productDivision != null ? productDivision.getId() : null)
            .setSizeScaleId(sizeScale != null ? sizeScale.getId() : null)
            .setPresMin(presMin)
            .setModifiedBy(USER)
            .setModifiedDate(LocalDateTime.now());
    }

    public FtwPriority prepareFtwPriority(long id, BrandDto brand, RmhGenderAgeDto rmhGenderAge, String sizeIndex, int priority) {
        return new FtwPriority()
            .setId(id)
            .setBrandId(brand.getId())
            .setRmhGenderAgeId(rmhGenderAge.getId())
            .setSizeIndex(sizeIndex)
            .setPriority(priority)
            .setModifiedDate(LocalDateTime.now())
            .setModifiedBy(USER);
    }

    public ArticleDto prepareArticle(Integer id, String name, String code, BrandDto brand, RmhGenderAgeDto age, RmhCategoryDto category, RmhProductTypeDto productType, RmhProductDivisionDto division, SizeScaleDto sizeScale, List<ArticleDto.SkuResponseDto> skus) {
        return new ArticleDto(
            id,
            name,
            code,
            brand.getName(),
            age.getName(),
            category.getName(),
            productType.getName(),
            division.getName(),
            sizeScale.getName(),
            LocalDateTime.now(),
            skus
        );
    }

    public TotalBuy prepareTotalBuy(ArticleDto article, String sizeIndex, int quantity) {
        return new TotalBuy(0, article.getId(), sizeIndex, quantity, USER, LocalDateTime.now());
    }

    public Matrix prepareMatrix(ArticleDto article, String sizeIndex, int quantity) {
        return new Matrix(0, article.getId().longValue(), sizeIndex, quantity, STORE_SUP_NUMBER);
    }
}
