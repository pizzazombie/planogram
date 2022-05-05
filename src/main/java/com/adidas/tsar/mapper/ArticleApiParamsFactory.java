package com.adidas.tsar.mapper;

import com.adidas.tsar.dto.ArticleSearchRequestDto;
import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class ArticleApiParamsFactory {

    public ArticleSearchRequestDto getArticleApiParamsForArticleIds(Collection<Long> articleIds) {
        return ArticleSearchRequestDto.builder()
            .ids(articleIds)
            .build();
    }

    public ArticleSearchRequestDto getArticleApiParamsForArticleCodes(Collection<String> articleCodes) {
        return ArticleSearchRequestDto.builder()
            .codes(articleCodes)
            .build();
    }




}
