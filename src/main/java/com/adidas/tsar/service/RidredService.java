package com.adidas.tsar.service;

import com.adidas.tsar.data.RidredRepository;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ridred.RidredCreateDto;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.mapper.RidredFactory;
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
public class RidredService {

    private final TsarMasterDataApiClient tsarMasterDataApiClient;
    private final RidredRepository ridredRepository;

    public Integer createRidred(List<RidredCreateDto> requestBody){

        final var articles = tsarMasterDataApiClient.getArticles(
            ArticleApiParamsFactory.getArticleApiParamsForArticleCodes(requestBody.stream().map(RidredCreateDto::getArticle).collect(Collectors.toSet()))
        ).getData().stream().collect(Collectors.toMap(ArticleDto::getCode, it -> it));

        final var newItems = requestBody.stream()
            .filter(it -> articles.containsKey(it.getArticle()))
            .map(createDto -> RidredFactory.getRidRed(createDto, articles.get(createDto.getArticle())))
            .collect(Collectors.toList());

        log.info("Save new RidRed: {}", newItems);
        ridredRepository.saveAll(newItems);
        return newItems.size();
    }

    @Transactional
    public void truncateRidRed() {
        log.info("Truncate RidRed");
        ridredRepository.truncate();
    }

    public void deleteRidRed(List<Long> ids) {
        log.info("Delete RidRed by ids: {}", ids);
        ridredRepository.deleteAllById(ids);
    }

    public long countOfRidRed() {
        return ridredRepository.count();
    }
}
