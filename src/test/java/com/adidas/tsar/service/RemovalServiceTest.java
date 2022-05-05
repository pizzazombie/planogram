package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.data.RemovalRepository;
import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSearchRequestDto;
import com.adidas.tsar.dto.removal.RemovalCreateDto;
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
public class RemovalServiceTest extends BaseIntegrationTest {

	@Autowired private  RemovalService removalService;
	@MockBean private RemovalRepository removalRepository;
	@Captor private ArgumentCaptor<List<Removal>> removalListCaptor;

	@Test
	void createRemovals_dataNotExist_created(){
		var removalList = List.of(
			new RemovalCreateDto(ARTICLE.getCode(), SAP_1, REMOVAL_NUM_1),
			new RemovalCreateDto(ARTICLE_2.getCode(), SAP_2, REMOVAL_NUM_2)
		);

		when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

		removalService.createRemovals(removalList);

		verify(removalRepository, atLeastOnce()).saveAll(removalListCaptor.capture());
		final var createdEntities = removalListCaptor.getValue();
		assertEquals(2, createdEntities.size());
		verifyRemoval(createdEntities.get(0), SAP_1, ARTICLE, REMOVAL_NUM_1);
		verifyRemoval(createdEntities.get(1), SAP_2, ARTICLE_2, REMOVAL_NUM_2);
	}

	@Test
	void deleteRemovals_dataExist_deleted(){

		var deleteIds = List.of(1L, 2L);

		removalService.deleteRemovals(deleteIds);

		verify(removalRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
		assertEquals(deleteIds, longListCaptor.getValue());
	}

	@Test
	void createRemovals_articleNotFound_created(){
		var removalList = List.of(
			new RemovalCreateDto(ARTICLE.getCode(), SAP_1, REMOVAL_NUM_1),
			new RemovalCreateDto(ARTICLE_2.getCode(), SAP_2, REMOVAL_NUM_2)
		);

		when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE_2)));

		removalService.createRemovals(removalList);

		verify(removalRepository, atLeastOnce()).saveAll(removalListCaptor.capture());
		final var createdEntities = removalListCaptor.getValue();
		assertEquals(1, createdEntities.size());
		verifyRemoval(createdEntities.get(0), SAP_2, ARTICLE_2, REMOVAL_NUM_2);
	}

	private void verifyRemoval(Removal removal, String sap, ArticleDto articleDto, String removalNumber){
		assertEquals(articleDto.getId().longValue(), removal.getArticleId());
		assertEquals(removalNumber, removal.getRemovalNumber());
		assertEquals(sap, removal.getSap());

	}

}
