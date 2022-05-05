package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.data.RidredRepository;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSearchRequestDto;
import com.adidas.tsar.dto.ridred.RidredCreateDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
	classes = PlanogramApplication.class,
	webEnvironment = SpringBootTest.WebEnvironment.MOCK,
	properties = {"spring.liquibase.enabled=false"}
)
public class RidredServiceTest extends BaseIntegrationTest {

	@Autowired private  RidredService ridredService;
	@MockBean private RidredRepository ridredRepository;
	@Captor private ArgumentCaptor<List<Ridred>> ridredListCaptor;

	@Test
	void createRidred_dataNotExist_created(){
		var ridredList = List.of(
			new RidredCreateDto(ARTICLE.getCode(), RID_1, RED_1),
			new RidredCreateDto(ARTICLE_2.getCode(), RID_2, RED_2)
		);

		when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

		ridredService.createRidred(ridredList);

		verify(ridredRepository, atLeastOnce()).saveAll(ridredListCaptor.capture());
		final var createdEntities = ridredListCaptor.getValue();
		assertEquals(2, createdEntities.size());
		verifyRidred(createdEntities.get(0), ARTICLE, RID_1, RED_1);
		verifyRidred(createdEntities.get(1), ARTICLE_2, RID_2, RED_2);
	}

	@Test
	void deleteRidred_dataExist_deleted(){

		var deleteIds = List.of(1L, 2L);

		ridredService.deleteRidRed(deleteIds);

		verify(ridredRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
		assertEquals(deleteIds, longListCaptor.getValue());
	}

	@Test
	void createRidred_articleNotFound_created(){
		var ridredList = List.of(
			new RidredCreateDto(ARTICLE.getCode(), RID_1, RED_1),
			new RidredCreateDto(ARTICLE_2.getCode(), RID_2, RED_2)
		);

		when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE_2)));

		ridredService.createRidred(ridredList);

		verify(ridredRepository, atLeastOnce()).saveAll(ridredListCaptor.capture());
		final var createdEntities = ridredListCaptor.getValue();
		assertEquals(1, createdEntities.size());
		verifyRidred(createdEntities.get(0), ARTICLE_2, RID_2, RED_2);
	}

	private void verifyRidred(Ridred ridred, ArticleDto articleDto, LocalDate rid, LocalDate red) {
		assertEquals(articleDto.getId().longValue(), ridred.getArticleId());
		assertEquals(rid, ridred.getRid());
		assertEquals(red, ridred.getRed());

	}
}
