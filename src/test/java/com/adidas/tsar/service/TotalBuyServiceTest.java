package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.data.TotalBuyRepository;
import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSearchRequestDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyCreateDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
public class TotalBuyServiceTest extends BaseIntegrationTest {

    @Autowired
    private TotalBuyService totalBuyService;

    @MockBean
    private TotalBuyRepository totalBuyRepository;

    @Captor
    private ArgumentCaptor<List<TotalBuy>> totalBuyListCaptor;

    @Test
    void findTotalBuy_dataExists_Ok() {
        var totalBuys = List.of(
            prepareTotalBuy(1L, ARTICLE, SIZE_INDEX_1, 1, USER),
            prepareTotalBuy(2L, ARTICLE_2, SIZE_INDEX_2, 2, USER)
        );
        when(totalBuyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(totalBuys));

        final var foundPage = totalBuyService.findTotalBuys(Pageable.ofSize(PAGE_SIZE));
        final var ids = foundPage.getContent().stream().map(TotalBuyResponse::getId).collect(Collectors.toList());
        assertEquals(List.of(1L, 2L), ids);
    }

    @Test
    void createTotalBuys_dataNotExists_CreatedAllTotalBuys() {
        var totalBuys = List.of(
            new TotalBuyCreateDto(ARTICLE.getCode(), SIZE_INDEX_1, 1),
            new TotalBuyCreateDto(ARTICLE_2.getCode(), SIZE_INDEX_2, 2)
        );
        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

        totalBuyService.createTotalBuys(totalBuys, USER);

        verify(totalBuyRepository, atLeastOnce()).saveAll(totalBuyListCaptor.capture());
        final var createdEntities = totalBuyListCaptor.getValue();
        createdEntities.sort(Comparator.comparingInt(TotalBuy::getQuantity));
        assertEquals(2, createdEntities.size());
        verifyTotalBuy(createdEntities.get(0), ARTICLE, SIZE_INDEX_1, 1);
        verifyTotalBuy(createdEntities.get(1), ARTICLE_2, SIZE_INDEX_2, 2);
    }

    @Test
    void deleteTotalBuys_dataExists_Deleted() {
        var deleteIds = List.of(1L, 2L);

        totalBuyService.deleteTotalBuys(deleteIds);

        verify(totalBuyRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
        assertEquals(deleteIds, longListCaptor.getValue());
    }

    @Test
    void createTotalBuys_oneArticleNotFound_CreatedOneTotalBuy() {
        var totalBuys = List.of(
            new TotalBuyCreateDto(ARTICLE.getCode(), SIZE_INDEX_1, 1),
            new TotalBuyCreateDto(ARTICLE_2.getCode(), SIZE_INDEX_2, 2)
        );
        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE_2)));

        totalBuyService.createTotalBuys(totalBuys, USER);

        verify(totalBuyRepository, atLeastOnce()).saveAll(totalBuyListCaptor.capture());
        final var createdEntities = totalBuyListCaptor.getValue();
        createdEntities.sort(Comparator.comparingInt(TotalBuy::getQuantity));
        assertEquals(1, createdEntities.size());
        verifyTotalBuy(createdEntities.get(0), ARTICLE_2, SIZE_INDEX_2, 2);
    }

    @Test
    void createTotalBuys_OneSkuNotFound_CreatedOneTotalBuy() {
        final String BAD_SIZE_INDEX = "003";

        var totalBuys = List.of(
            new TotalBuyCreateDto(ARTICLE.getCode(), BAD_SIZE_INDEX, 1),
            new TotalBuyCreateDto(ARTICLE_2.getCode(), SIZE_INDEX_2, 2)
        );
        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

        totalBuyService.createTotalBuys(totalBuys, USER);

        verify(totalBuyRepository, atLeastOnce()).saveAll(totalBuyListCaptor.capture());
        final var createdEntities = totalBuyListCaptor.getValue();
        createdEntities.sort(Comparator.comparingInt(TotalBuy::getQuantity));
        assertEquals(1, createdEntities.size());
        verifyTotalBuy(createdEntities.get(0), ARTICLE_2, SIZE_INDEX_2, 2);
    }

    private TotalBuy prepareTotalBuy(long id, ArticleDto article, String sizeIndex, int quantity, String user) {
        return new TotalBuy()
            .setId(id)
            .setArticleId(article.getId())
            .setSizeIndex(sizeIndex)
            .setQuantity(quantity)
            .setModifiedBy(user)
            .setModifiedDate(LocalDateTime.now());
    }

    private void verifyTotalBuy(TotalBuy totalBuy, ArticleDto article, String sizeIndex, int quantity) {
        assertEquals(article.getId().longValue(), totalBuy.getArticleId());
        assertEquals(sizeIndex, totalBuy.getSizeIndex());
        assertEquals(quantity, totalBuy.getQuantity());
    }

}
