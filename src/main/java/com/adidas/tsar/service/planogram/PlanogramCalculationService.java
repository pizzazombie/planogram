package com.adidas.tsar.service.planogram;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.config.FilterProperties;
import com.adidas.tsar.config.OrchestrationProperties;
import com.adidas.tsar.data.*;
import com.adidas.tsar.data.criteria.SearchCriteria;
import com.adidas.tsar.data.criteria.SearchOperation;
import com.adidas.tsar.data.criteria.SpecificationBuilder;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.dto.planogram.FinalPlanogramDecorator;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.planogram.PlanogramResponseDto;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.exceptions.EntityNotFoundException;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.mapper.KafkaEventFactory;
import com.adidas.tsar.mapper.PlanogramMapper;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import com.adidas.tsar.service.PublisherService;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramCalculationService {

    private final TsarMasterDataApiClient masterDataApiClient;
    private final PlanogramDao planogramDao;
    private final PlanogramRepository planogramRepository;
    private final MatrixDao matrixDao;
    private final MatrixRepository matrixRepository;
    private final RemovalRepository removalRepository;
    private final RidredRepository ridredRepository;
    private final PriorityCalculationService priorityCalculationService;
    private final PresMinCalculationService presMinCalculationService;
    private final SalesFloorQtyCalculationService salesFloorQtyCalculationService;
    private final FinalSalesFloorQtyService finalSalesFloorQtyService;
    private final PlanogramMapper planogramMapper;
    private final PublisherService publisherService;
    private final KafkaEventFactory kafkaEventFactory;
    private final OrchestrationProperties orchestrationProperties;
    private final FilterProperties filterProperties;

    @Value("${app.dictionary-blank-name}")
    private String dictionaryBlankName;

    @Value("${app.planogram.batch-size}")
    private int batchSize;

    public void calculatePlanogram() {
        Stopwatch fullProcessStopWatch = Stopwatch.createStarted();
        try {
            final var countOfMatrix = matrixRepository.count();
            log.info("Start calculate planogram priorities for {} matrix", countOfMatrix);
            final DictionariesCollectionUtils dictionaries = new DictionariesCollectionUtils()
                .with(BrandDto.class, masterDataApiClient.getBrands().getData(), dictionaryBlankName)
                .with(RmhGenderAgeDto.class, masterDataApiClient.getRmhGenderAges().getData(), dictionaryBlankName)
                .with(RmhCategoryDto.class, masterDataApiClient.getRmhCategories().getData(), dictionaryBlankName)
                .with(RmhProductTypeDto.class, masterDataApiClient.getRmhProductTypes().getData(), dictionaryBlankName)
                .with(RmhProductDivisionDto.class, masterDataApiClient.getRmhProductDivisions().getData(), dictionaryBlankName)
                .with(SizeScaleDto.class, masterDataApiClient.getSizeScales().getData(), dictionaryBlankName)
                .with(StoreDto.class, masterDataApiClient.getStores().getData(), StoreDto::getId, StoreDto::getSap, null);
            final var presMinMap = presMinCalculationService.preparePresMinMap();
            final var ridReds = ridredRepository.findAll();
            final var removals = removalRepository.findAll();
            final var matricesByArticleNumber = matrixDao.findAll()
                .stream().collect(groupingBy(Matrix::getArticleId));

            planogramDao.truncatePlanogram();
            Streams.stream(Iterables.partition(matricesByArticleNumber.entrySet(), batchSize))
                .forEach(pageOfMatrixByArticleId -> {
                    final var chunk = fetchArticlesAndMap(pageOfMatrixByArticleId.stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    final var results = calculatePlanogram(chunk, dictionaries, presMinMap, ridReds, removals);
                    savePlanograms(results);
                });

            sendPlanogramCalculateFinishEvent();
            log.info("[STOPWATCH] Finish calculating priorities: {} ms", fullProcessStopWatch.stop().elapsed(TimeUnit.MILLISECONDS));
        } finally {
            if (fullProcessStopWatch.isRunning()) fullProcessStopWatch.stop();
        }
    }

    private List<MatricesByArticleImpl> fetchArticlesAndMap(Map<Long, List<Matrix>> articlesByMatrix) {
        return masterDataApiClient.getArticles(
            ArticleApiParamsFactory.getArticleApiParamsForArticleIds(articlesByMatrix.keySet())
        ).getData().stream()
            .map(it -> new MatricesByArticleImpl(it, articlesByMatrix.get(it.getId())))
            .collect(toList());
    }

    private List<Planogram> calculatePlanogram(List<MatricesByArticleImpl> data, DictionariesCollectionUtils dictionaries, Map<VmStandardKey, Integer> presMinMap, List<Ridred> ridReds, List<Removal> removals) {
        return data.stream().flatMap(matricesChunk -> {
            final var prioritiesChunk = new PrioritiesDecorator(matricesChunk);
            priorityCalculationService.populatePriority(dictionaries, prioritiesChunk);
            matricesChunk.setPresMin(presMinCalculationService.getPresMin(presMinMap, dictionaries, matricesChunk));
            final var salesFloorQtyChunk = new SalesFloorQtyDecorator(prioritiesChunk, matricesChunk.getPresMin());
            salesFloorQtyCalculationService.populateSalesFloorQty(salesFloorQtyChunk);
            final var articleRidReds = ridReds.stream().filter(it -> it.getArticleId() == matricesChunk.getArticle().getId()).collect(toList());
            final var articleRemovals = removals.stream().filter(it -> it.getArticleId() == matricesChunk.getArticle().getId()).collect(toList());
            final var finalPlanogramDecorator = new FinalPlanogramDecorator(salesFloorQtyChunk, articleRidReds, articleRemovals);
            finalSalesFloorQtyService.populateFinalSalesFloorQty(finalPlanogramDecorator);

            final var planograms = finalPlanogramDecorator.getItems().entrySet().stream()
                .map(entry -> new Planogram(
                    null,
                    matricesChunk.getArticle().getCode(),
                    dictionaries.getDictionaryItem(StoreDto.class, entry.getKey().getStoreId()).map(StoreDto::getSap)
                        .orElseThrow(() -> new EntityNotFoundException("Store with id: " + entry.getKey().getStoreId() + " is not found")),
                    matricesChunk.getArticle().getSkus().stream().filter(it -> it.getSizeIndex().equals(entry.getKey().getSizeIndex())).findAny()
                        .orElseThrow(() -> new EntityNotFoundException("Sku with sizeIndex " + entry.getKey().getSizeIndex() + " is not found")).getGtin(),
                    entry.getKey().getSizeIndex(),
                    finalPlanogramDecorator.getPriority(entry.getKey().getSizeIndex()),
                    matricesChunk.getPresMin(),
                    entry.getValue().getSalesFloorQty(),
                    entry.getValue().getFinalSalesFloorQty(),
                    entry.getValue().isIgnoreForReverseReplenishment(),
                    LocalDateTime.now()
                ))
                .collect(toList());
            return planograms.stream();
        }).collect(toList());
    }

    private void savePlanograms(List<Planogram> planograms) {
        planogramDao.saveAll(planograms);
        log.info("SAVED {} RESULTS", planograms.size());
    }

    private void sendPlanogramCalculateFinishEvent() {
        log.info("Publishing planogram calculating finish");
        final var event = kafkaEventFactory.getCommandResultEvent(orchestrationProperties.getCalculate());
        publisherService.publishEvent(event);
    }

    public long countOfPlanograms() {
        return planogramDao.count();
    }

    public Page<PlanogramResponseDto> getPlanograms(String filter, Pageable paging) {
        final var specification = new SpecificationBuilder<Planogram>()
                .withSearchString(filter)
                .with(this::buildSearchCriteria)
                .build();

        final var planogramResultPage = planogramRepository.findAll(specification, paging);

        final var articleCodes = planogramResultPage.stream().map(Planogram::getArticleCode).collect(Collectors.toSet());
        final var skuByArticleCodeMap = masterDataApiClient.getArticles(
            ArticleApiParamsFactory.getArticleApiParamsForArticleCodes(articleCodes)).getData().stream()
            .collect(toMap(
                ArticleDto::getCode,
                article -> article.getSkus().stream()
                    .collect(toMap(ArticleDto.SkuResponseDto::getSizeIndex, Function.identity(),
                        (item1, item2) -> item1))
            ));

        return planogramResultPage.map(planogram -> {
            var sku = Optional.ofNullable(skuByArticleCodeMap.get(planogram.getArticleCode())).map(it -> it.get(planogram.getSizeIndex()));
            return planogramMapper.toResponse(planogram, sku.orElse(null));
        });
    }

    private SearchCriteria buildSearchCriteria(String fieldArg, String operationArg, Object valueArg) {
        final var field = Optional.ofNullable(filterProperties.getFieldsMapping().get(fieldArg)).orElse(fieldArg);
        final var operation = SearchOperation.getSearchOperation(operationArg.toLowerCase());
        if (operation == null)
            throw new IllegalArgumentException("The filter operationArg " + operationArg + " has incorrect valueArg");
        return new SearchCriteria(Arrays.asList(StringUtils.split(field, '.')), operation, valueArg);
    }
}
