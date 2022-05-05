package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;

import java.util.List;

public interface MatricesByArticle {
    ArticleDto getArticle();

    List<Matrix> getMatrices();

    PlanogramProductType getPlanogramProductType();
}
