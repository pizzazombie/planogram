package com.adidas.tsar.service.planogram;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.data.MatrixDao;
import com.adidas.tsar.data.MatrixRepository;
import com.adidas.tsar.data.PlanogramDao;
import com.adidas.tsar.data.RemovalRepository;
import com.adidas.tsar.data.RidredRepository;
import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhCategoryDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.RmhProductDivisionDto;
import com.adidas.tsar.dto.RmhProductTypeDto;
import com.adidas.tsar.dto.SizeScaleDto;
import com.adidas.tsar.dto.planogram.FinalPlanogramDecorator;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.mapper.ArticleApiParamsFactory;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramCalculationService {

    private final PlanogramDao planogramDao;
    private final TsarMasterDataApiClient masterDataApiClient;
    private final MatrixRepository matrixRepository;
    private final MatrixDao matrixDao;
    private final RemovalRepository removalRepository;
    private final RidredRepository ridredRepository;
    private final PriorityCalculationService priorityCalculationService;
    private final PresMinCalculationService presMinCalculationService;
    private final SalesFloorQtyCalculationService salesFloorQtyCalculationService;
    private final FinalSalesFloorQtyService finalSalesFloorQtyService;

    @Value("${app.planogram.batch-size}")
    private int batchSize;

    public void calculatePlanogram() {
        Stopwatch fullProcessStopWatch = Stopwatch.createStarted();
        try {
            final var countOfMatrix = matrixRepository.count();
            log.info("Start calculate planogram priorities for {} matrix", countOfMatrix);
            final DictionariesCollectionUtils dictionaries = new DictionariesCollectionUtils(List.of(
                Pair.of(BrandDto.class, masterDataApiClient.getBrands().getData()),
                Pair.of(RmhGenderAgeDto.class, masterDataApiClient.getRmhGenderAges().getData()),
                Pair.of(RmhCategoryDto.class, masterDataApiClient.getRmhCategories().getData()),
                Pair.of(RmhProductTypeDto.class, masterDataApiClient.getRmhProductTypes().getData()),
                Pair.of(RmhProductDivisionDto.class, masterDataApiClient.getRmhProductDivisions().getData()),
                Pair.of(SizeScaleDto.class, masterDataApiClient.getSizeScales().getData())
            ));
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
            final var presMin = presMinCalculationService.getPresMin(presMinMap, dictionaries, matricesChunk);
            final var salesFloorQtyChunk = new SalesFloorQtyDecorator(prioritiesChunk, presMin);
            salesFloorQtyCalculationService.populateSalesFloorQty(salesFloorQtyChunk);
            final var articleRidReds = ridReds.stream().filter(it -> it.getArticleId() == matricesChunk.getArticle().getId().longValue()).collect(toList());
            final var articleRemovals = removals.stream().filter(it -> it.getArticleId() == matricesChunk.getArticle().getId().longValue()).collect(toList());
            final var finalPlanogramDecorator = new FinalPlanogramDecorator(salesFloorQtyChunk, articleRidReds, articleRemovals);
            finalSalesFloorQtyService.populateFinalSalesFloorQty(finalPlanogramDecorator);

            final var planograms = finalPlanogramDecorator.getItems().entrySet().stream()
                .map(entry -> new Planogram(
                    matricesChunk.getArticle().getCode(),
                    entry.getKey().getSap(),
                    entry.getKey().getSizeIndex(),
                    finalPlanogramDecorator.getPriority(entry.getKey().getSizeIndex()),
                    matricesChunk.getPresMin(),
                    entry.getValue().getSalesFloorQty(),
                    entry.getValue().getFinalSalesFloorQty(),
                    entry.getValue().getIgnoreForReverseReplenishment()
                ))
                .collect(toList());
            return planograms.stream();
        }).collect(toList());
    }

    private void savePlanograms(List<Planogram> planograms) {
        planogramDao.saveAll(planograms);
        log.info("SAVED {} RESULTS", planograms.size());
    }

    public long countOfPlanograms() {
        return planogramDao.count();
    }

}
