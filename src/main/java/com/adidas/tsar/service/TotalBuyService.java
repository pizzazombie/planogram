package com.adidas.tsar.service;

import com.adidas.tsar.data.TotalBuyRepository;
import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSizeIndexesDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyCreateDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyResponse;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.mapper.TotalBuyFactory;
import com.adidas.tsar.mapper.TotalBuyResponseMapper;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TotalBuyService {

	private final TsarMasterDataApiClient tsarMasterDataApiClient;
	private final TotalBuyRepository totalBuyRepository;
	private final TotalBuyResponseMapper totalBuyResponseMapper;

	public Page<TotalBuyResponse> findTotalBuys(Pageable paging) {
		final var totalBuys = totalBuyRepository.findAll(paging);
		final var articles = tsarMasterDataApiClient.getArticles(
			ArticleApiParamsFactory.getArticleApiParamsForArticleIds(totalBuys.stream().map(TotalBuy::getArticleId).collect(Collectors.toSet()))
		).getData().stream().collect(Collectors.toMap(ArticleDto::getId, it -> it));
		return totalBuys.map(it -> totalBuyResponseMapper.toResponse(it, articles.get(it.getArticleId())));
	}

	public Integer createTotalBuys(List<TotalBuyCreateDto> requestBody, String currentUser) {

		final var articlesWithSizeIndices = tsarMasterDataApiClient.getArticles(
			ArticleApiParamsFactory.getArticleApiParamsForArticleCodes(requestBody.stream().map(TotalBuyCreateDto::getArticle).collect(Collectors.toSet()))
		).getData().stream().collect(Collectors.toMap(ArticleDto::getCode,
			it -> new ArticleSizeIndexesDto(it, it.getSkus().stream()
				.map(ArticleDto.SkuResponseDto::getSizeIndex)
				.collect(Collectors.toSet()))
		));

		final var newItems = requestBody.stream()
			.filter(it -> articlesWithSizeIndices.containsKey(it.getArticle())
				&& articlesWithSizeIndices.get(it.getArticle()).getSizeIndexes().contains(it.getSizeIndex()))
			.map(createDto -> TotalBuyFactory.getTotalBuy(createDto, articlesWithSizeIndices.get(createDto.getArticle()).getArticleDto(), currentUser))
			.collect(Collectors.toList());

		log.info("Save new Total Buys: {}", newItems);
		totalBuyRepository.saveAll(newItems);
		return newItems.size();
	}

	public void deleteTotalBuys(List<Long> ids) {
		log.info("Delete Total Buys by ids: {}", ids);
		totalBuyRepository.deleteAllById(ids);
	}

	@Transactional
	public void truncateTotalBuys() {
		log.info("Truncate Total Buys");
		totalBuyRepository.truncate();
	}

	public long countOfTotalBuys() {
		return totalBuyRepository.count();
	}

}
