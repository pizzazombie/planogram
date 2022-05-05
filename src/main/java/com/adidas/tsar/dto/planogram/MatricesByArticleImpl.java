package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class MatricesByArticleImpl implements MatricesByArticle {
    private static final int MIN_PRIORITY = 0;

    private final ArticleDto article;
    private final List<Matrix> matrices;
    private final Map<String, Integer> quantitySumBySizeIndex;
    private final PlanogramProductType planogramProductType;

    @Setter
    private int presMin;

    public MatricesByArticleImpl(ArticleDto article, List<Matrix> matrices) {
        this.article = article;
        this.matrices = matrices;
        this.planogramProductType = getPlanogramProductType(article);
        this.quantitySumBySizeIndex = matrices.stream().collect(Collectors.groupingBy(Matrix::getSizeIndex, Collectors.summingInt(Matrix::getQuantity)));
    }

    private PlanogramProductType getPlanogramProductType(ArticleDto article) {
        if (Optional.ofNullable(article.getProductDivision()).map(division -> division.equalsIgnoreCase("footwear")).orElse(false)
            && !StringUtils.containsIgnoreCase(article.getProductType(), "slide")) {
            return PlanogramProductType.FOOTWEAR;
        } else {
            return PlanogramProductType.APPAREL;
        }
    }
}