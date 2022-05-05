package com.adidas.tsar.mapper;

import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.dto.totalbuy.TotalBuyCreateDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TotalBuyFactory {

    public TotalBuy getTotalBuy(TotalBuyCreateDto createDto, ArticleDto article, String user) {
        return new TotalBuy()
            .setArticleId(article.getId())
            .setSizeIndex(createDto.getSizeIndex())
            .setQuantity(createDto.getQuantity())
            .setModifiedBy(user)
            .setModifiedDate(LocalDateTime.now());
    }

}
