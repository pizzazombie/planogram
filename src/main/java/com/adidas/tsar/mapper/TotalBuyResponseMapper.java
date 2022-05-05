package com.adidas.tsar.mapper;

import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.dto.totalbuy.TotalBuyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TotalBuyResponseMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "articleName", source = "article.name")
    @Mapping(target = "articleCode", source = "article.code")
    @Mapping(target = "modified", source = "source.modifiedDate")
    TotalBuyResponse toResponse(TotalBuy source, ArticleDto article);

}
