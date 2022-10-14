package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.StoreDto;
import com.adidas.tsar.dto.removal.RemovalCreateDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RemovalFactory {

    public Removal getRemoval(RemovalCreateDto createDto, ArticleDto articleDto, StoreDto storeDto) {
        return new Removal()
            .setArticleId(articleDto.getId())
            .setStoreId(storeDto.getId())
            .setRemovalNumber(createDto.getRemovalNum());
    }
}
