package com.adidas.tsar.rest.feign;


import com.adidas.tsar.dto.*;
import com.adidas.tsar.exceptions.ApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Retry(name = "masterDataApi")
@FeignClient(name = TsarMasterDataApiClient.API_NAME, url = "${md.service.url}")
public interface TsarMasterDataApiClient {

    String API_NAME = "TsarApiClient";
    int FIRST_PAGE = 0;
    int MAX_PAGE_SIZE = Integer.MAX_VALUE;

    @GetMapping("/brands")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getBrandsFallback")
    BaseResponse<List<BrandDto>> getBrands(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<BrandDto>> getBrands() {
        return getBrands(FIRST_PAGE, MAX_PAGE_SIZE);
    }

    default BaseResponse<List<BrandDto>> getBrandsFallback(ApiException exception) {
        throw exception;
    }

    @GetMapping("/rmhgenderages")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getRmhGenderAgesFallback")
    BaseResponse<List<RmhGenderAgeDto>> getRmhGenderAges(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<RmhGenderAgeDto>> getRmhGenderAges() {
        return getRmhGenderAges(FIRST_PAGE, MAX_PAGE_SIZE);
    }

    default BaseResponse<List<RmhGenderAgeDto>> getRmhGenderAgesFallback(ApiException e) {
        throw e;
    }

    @GetMapping("/rmhcategories")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getRmhCategoriesFallback")
    BaseResponse<List<RmhCategoryDto>> getRmhCategories(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<RmhCategoryDto>> getRmhCategories() {
        return getRmhCategories(FIRST_PAGE, MAX_PAGE_SIZE);
    }

    default BaseResponse<List<RmhCategoryDto>> getRmhCategoriesFallback(ApiException e) {
        throw e;
    }

    @GetMapping("/rmhproducttypes")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getRmhProductTypesFallback")
    BaseResponse<List<RmhProductTypeDto>> getRmhProductTypes(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<RmhProductTypeDto>> getRmhProductTypes() {
        return getRmhProductTypes(FIRST_PAGE, MAX_PAGE_SIZE);
    }

    default BaseResponse<List<RmhProductTypeDto>> getRmhProductTypesFallback(ApiException e) {
        throw e;
    }

    @GetMapping("/rmhproductdivisions")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getRmhProductDivisionsFallback")
    BaseResponse<List<RmhProductDivisionDto>> getRmhProductDivisions(
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<RmhProductDivisionDto>> getRmhProductDivisions() {
        return getRmhProductDivisions(FIRST_PAGE, MAX_PAGE_SIZE);
    }

    default BaseResponse<List<RmhProductDivisionDto>> getRmhProductDivisionsFallback(ApiException e) {
        throw e;
    }

    default BaseResponse<List<ArticleDto>> getArticles(ArticleSearchRequestDto articleSearchRequestDto) {
        return getArticles(articleSearchRequestDto, FIRST_PAGE, MAX_PAGE_SIZE);
    }

    @PostMapping("/articles/search")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getArticlesFallback")
    BaseResponse<List<ArticleDto>> getArticles(
        @RequestBody ArticleSearchRequestDto articleSearchRequestDto,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    );

    default BaseResponse<List<ArticleDto>> getArticlesFallback(ApiException e) {
        throw e;
    }

    @GetMapping("/sizescales")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getSizeScalesFallback")
    BaseResponse<List<SizeScaleDto>> getSizeScales();

    default BaseResponse<List<SizeScaleDto>> getSizeScalesFallback(ApiException e) {
        throw e;
    }

    @GetMapping("/stores")
    @CircuitBreaker(name = API_NAME, fallbackMethod = "getStoresFallback")
    BaseResponse<List<StoreDto>> getStores();

    default BaseResponse<List<StoreDto>> getStoresFallback(ApiException e) {
        throw e;
    }

}
