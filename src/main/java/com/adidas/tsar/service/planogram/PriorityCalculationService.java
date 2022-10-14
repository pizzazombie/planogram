package com.adidas.tsar.service.planogram;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.data.FtwPriorityRepository;
import com.adidas.tsar.data.TotalBuyRepository;
import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKey;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKeyDto;
import com.adidas.tsar.dto.planogram.PrioritiesDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriorityCalculationService {

    private static final int EMPTY_PRIORITY = 0;
    private static final String PLANOGRAM_CALC_SECTION_TITLE = "Planogram calculating";

    private final FtwPriorityRepository ftwPriorityRepository;
    private final TotalBuyRepository totalBuyRepository;

    public void populatePriority(DictionariesCollectionUtils dictionaries, PrioritiesDecorator batch) {
        log.trace("Calculate {} priority for {} Article", batch.getPlanogramProductType().name(), batch.getArticle().getCode());
        switch (batch.getPlanogramProductType()) {
            case FOOTWEAR:
                populateFtwPriorities(dictionaries, batch);
                break;
            case APPAREL:
                populateAppPriorities(batch);
                break;
        }
    }

    public void populateFtwPriorities(DictionariesCollectionUtils dictionaries, PrioritiesDecorator batch) {
        final var ftwPriorityKeysInBatch = batch.getSizeIndexes().stream()
            .map(it -> mapArticleToFtwPriorityKey(dictionaries, batch.getArticle(), it))
            .collect(Collectors.toList());

        final var ftwPriorityMap = ftwPriorityRepository.findAllByKeys(ftwPriorityKeysInBatch)
            .stream().collect(Collectors.toMap(FtwPriority::buildKey, Function.identity()));

        int maxPriorityInBatch = ftwPriorityKeysInBatch.stream()
            .reduce(EMPTY_PRIORITY,
                (maxPriority, item) -> {
                    final var priority = Optional.ofNullable(ftwPriorityMap.get(item)).map(FtwPriority::getPriority);
                    priority.ifPresent(it -> batch.setPriority(item.getSizeIndex(), it));
                    return Integer.max(maxPriority, priority.orElse(maxPriority));
                },
                Integer::max
            );

        //Populate empty priorities by default process
        maxPriorityInBatch = batch.getPrioritiesBySizeIndex().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .filter(it -> it.getValue().equals(EMPTY_PRIORITY))
            .reduce(
                maxPriorityInBatch,
                (priority, item) -> {
                    batch.setPriority(item.getKey(), ++priority);
                    return priority;
                },
                (integer1, integer2) -> integer2
            );
        log.trace("Max priority for {} article is {}", batch.getArticle().getCode(), maxPriorityInBatch);
    }

    public void populateAppPriorities(final PrioritiesDecorator batch) {
        final var totalBuys = totalBuyRepository.findTotalBuyByArticleIdOrderByQuantityDescSizeIndexAsc(batch.getArticle().getId());
        //setup priority from totalBuys ordered by totalBuy.quantity
        var maxPriorityInBatch = totalBuys.stream()
            .filter(it -> batch.getSizeIndexes().contains(it.getSizeIndex()))
            .reduce(EMPTY_PRIORITY,
                (priority, totalBuy) -> {
                    batch.setPriority(totalBuy.getSizeIndex(), ++priority);
                    return priority;
                },
                (integer1, integer2) -> integer2
            );

        final var sizeIndexesWithEmptyPriority = batch.getPrioritiesBySizeIndex().entrySet().stream()
            .filter(it -> it.getValue().equals(EMPTY_PRIORITY))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        //if totalBuy doesn't exists for some article|sizeIndex - populate priority from matrix
        //setup priority from matrix ordered by matrix.quantity
        maxPriorityInBatch = batch.getQuantitySumBySizeIndex().entrySet().stream()
            .filter(it -> sizeIndexesWithEmptyPriority.contains(it.getKey()))
            .sorted(
                ((Comparator<Map.Entry<String, Integer>>) (qty1, qty2) -> qty2.getValue().compareTo(qty1.getValue()))
                    .thenComparing(Map.Entry::getKey)
            )
            .reduce(maxPriorityInBatch,
                (priority, entry) -> {
                    batch.setPriority(entry.getKey(), ++priority);
                    return priority;
                },
                (integer1, integer2) -> integer2
            );
        log.trace("Max priority for {} article is {}", batch.getArticle().getCode(), maxPriorityInBatch);
    }

    private FtwPriorityKey mapArticleToFtwPriorityKey(DictionariesCollectionUtils dictionaries, ArticleDto article, String sizeIndex) {
        return new FtwPriorityKeyDto(
            dictionaries.getOrThrow(BrandDto.class, article.getBrand(), PLANOGRAM_CALC_SECTION_TITLE).getId(),
            dictionaries.getOrThrow(RmhGenderAgeDto.class, article.getGenderAge(), PLANOGRAM_CALC_SECTION_TITLE).getId(),
            sizeIndex
        );
    }
}
