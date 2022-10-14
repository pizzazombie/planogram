package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.StoreDto;
import com.adidas.tsar.dto.matrix.MatrixCreateDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatrixFactory {

    public Matrix getMatrix(MatrixCreateDto createDto, ArticleDto articleDto, StoreDto storeDto) {
        return new Matrix()
            .setArticleId(articleDto.getId())
            .setSizeIndex(createDto.getSizeIndex())
            .setQuantity(createDto.getQuantity())
            .setStoreId(storeDto.getId());
    }
}
