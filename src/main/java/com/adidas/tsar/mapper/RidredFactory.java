package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ridred.RidredCreateDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RidredFactory {

    public Ridred getRidRed(RidredCreateDto createDto, ArticleDto articleDto){
        return new Ridred()
                .setArticleId(articleDto.getId())
                .setRid(createDto.getRid())
                .setRed(createDto.getRed());
    }
}
