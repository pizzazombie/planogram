package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.data.MatrixRepository;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.ArticleSearchRequestDto;
import com.adidas.tsar.dto.matrix.MatrixCreateDto;
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

import java.util.Comparator;
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
public class MatrixServiceTest extends BaseIntegrationTest {

    @Autowired private MatrixService matrixService;

    @MockBean private MatrixRepository matrixRepository;

    @Captor
    private ArgumentCaptor<List<Matrix>> matrixListCaptor;

    @Test
    void createMatrix_dataNotExist_created(){
        var matrixList = List.of(
            new MatrixCreateDto(ARTICLE.getCode(), SAP_1, SIZE_INDEX_1, 1),
            new MatrixCreateDto(ARTICLE_2.getCode(), SAP_2, SIZE_INDEX_2, 6)
        );

        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

        matrixService.createMatrix(matrixList);

        verify(matrixRepository, atLeastOnce()).saveAll(matrixListCaptor.capture());
        final var createdEntities = matrixListCaptor.getValue();
        assertEquals(2, createdEntities.size());
        createdEntities.sort(Comparator.comparingInt(Matrix::getQuantity));
        verifyMatrix(createdEntities.get(0), SAP_1, ARTICLE, SIZE_INDEX_1, 1);
        verifyMatrix(createdEntities.get(1), SAP_2, ARTICLE_2, SIZE_INDEX_2, 6);
    }

    @Test
    void deleteMatrix_dataExist_deleted(){

        var deleteIds = List.of(1L, 2L);

        matrixService.deleteMatrix(deleteIds);

        verify(matrixRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
        assertEquals(deleteIds, longListCaptor.getValue());
    }

    @Test
    void createMatrix_articleNotFound_created(){
        var matrixList = List.of(
            new MatrixCreateDto(ARTICLE.getCode(), SAP_1, SIZE_INDEX_1, 1),
            new MatrixCreateDto(ARTICLE_2.getCode(), SAP_2, SIZE_INDEX_2, 6)
        );

        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of( ARTICLE_2)));

        matrixService.createMatrix(matrixList);

        verify(matrixRepository, atLeastOnce()).saveAll(matrixListCaptor.capture());
        final var createdEntities = matrixListCaptor.getValue();
        assertEquals(1, createdEntities.size());
        createdEntities.sort(Comparator.comparingInt(Matrix::getQuantity));
        verifyMatrix(createdEntities.get(0), SAP_2, ARTICLE_2, SIZE_INDEX_2, 6);
    }

    @Test
    void createMatrix_OneSkuNotFound_CreatedOneMatrix() {
        final String BAD_SIZE_INDEX = "003";

        var matrixList = List.of(
            new MatrixCreateDto(ARTICLE.getCode(), SAP_1, BAD_SIZE_INDEX, 1),
            new MatrixCreateDto(ARTICLE_2.getCode(), SAP_2, SIZE_INDEX_2, 6)
        );

        when(tsarMasterDataApiClient.getArticles(any(ArticleSearchRequestDto.class))).thenReturn(buildArrayBaseResponse(List.of(ARTICLE, ARTICLE_2)));

        matrixService.createMatrix(matrixList);

        verify(matrixRepository, atLeastOnce()).saveAll(matrixListCaptor.capture());
        final var createdEntities = matrixListCaptor.getValue();
        assertEquals(1, createdEntities.size());
        verifyMatrix(createdEntities.get(0), SAP_2, ARTICLE_2, SIZE_INDEX_2, 6);
    }

    private void verifyMatrix(Matrix matrix, String sap, ArticleDto articleDto, String sizeIndex, int quantity){
        assertEquals(articleDto.getId().longValue(), matrix.getArticleId());
        assertEquals(sizeIndex, matrix.getSizeIndex());
        assertEquals(quantity, matrix.getQuantity());
        assertEquals(sap, matrix.getSap());

    }
}
