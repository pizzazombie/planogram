package com.adidas.tsar.service;

import com.adidas.tsar.data.RemovalRepository;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.removal.RemovalCreateDto;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.mapper.RemovalFactory;
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
public class RemovalService {

    private final TsarMasterDataApiClient tsarMasterDataApiClient;
    private final RemovalRepository removalRepository;

    public Integer createRemovals(List<RemovalCreateDto> requestBody){

        final var articles = tsarMasterDataApiClient.getArticles(
                ArticleApiParamsFactory.getArticleApiParamsForArticleCodes(requestBody.stream().map(RemovalCreateDto::getArticle).collect(Collectors.toSet()))
        ).getData().stream().collect(Collectors.toMap(ArticleDto::getCode, it -> it));

        final var newItems = requestBody.stream()
            .filter(it -> articles.containsKey(it.getArticle()))
            .map(createDto -> RemovalFactory.getRemoval(createDto, articles.get(createDto.getArticle())))
            .collect(Collectors.toList());

        log.info("Save new Removals: {}", newItems);
        removalRepository.saveAll(newItems);
        return newItems.size();
    }

    @Transactional
    public void truncateRemovals() {
        log.info("Truncate Removals");
        removalRepository.truncate();
    }

    public void deleteRemovals(List<Long> ids) {
        log.info("Delete Removals by ids: {}", ids);
        removalRepository.deleteAllById(ids);
    }

    public long countOfRemovals() {
        return removalRepository.count();
    }
}
