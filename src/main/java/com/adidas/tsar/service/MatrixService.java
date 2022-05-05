package com.adidas.tsar.service;

import com.adidas.tsar.data.MatrixRepository;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSizeIndexesDto;
import com.adidas.tsar.dto.StoreDto;
import com.adidas.tsar.dto.matrix.MatrixCreateDto;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.mapper.MatrixFactory;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MatrixService {

    private final TsarMasterDataApiClient tsarMasterDataApiClient;
    private final MatrixRepository matrixRepository;

    public Integer createMatrix(List<MatrixCreateDto> requestBody){

        final var articlesWithSizeIndices = tsarMasterDataApiClient.getArticles(
            ArticleApiParamsFactory.getArticleApiParamsForArticleCodes(requestBody.stream().map(MatrixCreateDto::getArticle).collect(Collectors.toSet()))
        ).getData().stream().collect(Collectors.toMap(ArticleDto::getCode,
            it -> new ArticleSizeIndexesDto(it, it.getSkus().stream()
                .map(ArticleDto.SkuResponseDto::getSizeIndex)
                .collect(Collectors.toSet()))
        ));

        final var stores = tsarMasterDataApiClient.getStores().getData().stream().collect(Collectors.toMap(StoreDto::getSap, it -> it));

        final var newItems = requestBody.stream()
            .filter(it -> articlesWithSizeIndices.containsKey(it.getArticle())
                && articlesWithSizeIndices.get(it.getArticle()).getSizeIndexes().contains(it.getSizeIndex())
                && stores.containsKey(it.getSap()))
            .map(createDto -> MatrixFactory.getMatrix(createDto, articlesWithSizeIndices.get(createDto.getArticle()).getArticleDto()))
            .collect(Collectors.toList());

        log.info("Save new Matrix: {}", newItems);
        matrixRepository.saveAll(newItems);
        return newItems.size();
    }

    @Transactional
    public void truncateMatrix() {
        log.info("Truncate Matrix");
        matrixRepository.truncate();
    }

    public void deleteMatrix(List<Long> ids) {
        log.info("Delete Matrix by ids: {}", ids);
        matrixRepository.deleteAllById(ids);
    }

    public long countOfMatrix() {
        return matrixRepository.count();
    }
}
