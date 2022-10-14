package com.adidas.tsar.service.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import com.adidas.tsar.dto.planogram.SalesFloorQtyItem;
import com.adidas.tsar.dto.planogram.StoreAndSizeKey;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class SalesFloorQtyCalculationService {

    public void populateSalesFloorQty(SalesFloorQtyDecorator chunk) {
        log.trace("Calculate {} SalesFloorQty for {} Article", chunk.getPlanogramProductType().name(), chunk.getArticle().getCode());
        switch (chunk.getPlanogramProductType()) {
            case FOOTWEAR:
                populateFtwSalesFloorQty(chunk);
                break;
            case APPAREL:
                populateAppSalesFloorQty(chunk);
                break;
        }
    }

    void populateFtwSalesFloorQty(SalesFloorQtyDecorator chunk) {
        final var matriciesByStoreMap = chunk.getMatrices().stream()
            .collect(groupingBy(Matrix::getStoreId));

        final var salesFloorQtyMap = matriciesByStoreMap.values().stream()
            .flatMap(matricesByStore -> {
                // for footwear articles SFQ = 1 for first sizeIndex with quantity > 0
                var priorityMatrix = matricesByStore.stream()
                    .filter(it -> it.getQuantity() >= 0)
                    .min(Comparator.comparingInt(matrix -> chunk.getPriority(matrix.getSizeIndex())));

                return matricesByStore.stream()
                    .map(matrix -> {
                        int salesFloorQty = 0;
                        if (priorityMatrix.isPresent() && matrix.equals(priorityMatrix.get())) {
                            salesFloorQty = 1;
                        }
                        return Pair.of(new StoreAndSizeKey(matrix), salesFloorQty);
                    });
            })
            .collect(toMap(Pair::getKey, pair -> new SalesFloorQtyItem(pair.getValue(), 0, false)));
        chunk.getSalesFloorQtyByStoreAndSize().putAll(salesFloorQtyMap);
    }

    void populateAppSalesFloorQty(SalesFloorQtyDecorator chunk) {
        final var matriciesByStoreMap = chunk.getMatrices().stream()
            .collect(groupingBy(Matrix::getStoreId));

        final var salesFloorQtyMap = matriciesByStoreMap.values().stream()
            .flatMap(matricesByStore -> {
                final var sorted = matricesByStore.stream()
                    .sorted(Comparator.comparingInt(it -> chunk.getPriority(it.getSizeIndex())))
                    .map(it -> MutablePair.of(it, 0))
                    .collect(toList());

                // sum of salesFloorQty upper limit
                int salesFloorQtyForDistributing = Math.min(
                    chunk.getPresMin(),
                    sorted.stream()
                        .map(it -> it.getKey().getQuantity())
                        .reduce(Integer::sum)
                        .orElse(0));

                // cycle by matrix while sum of salesFloorQty > presMin
                var iter = Iterables.cycle(sorted).iterator();
                while (salesFloorQtyForDistributing > 0) {
                    final var next = iter.next();
                    if (next.getKey().getQuantity() >= next.getValue() + 1) {
                        next.setValue(next.getValue() + 1);
                        salesFloorQtyForDistributing--;
                    }
                }

                return sorted.stream();
            }).collect(Collectors.toMap(pair -> new StoreAndSizeKey(pair.getKey()), pair -> new SalesFloorQtyItem(pair.getValue(), 0, false),
                (item1, item22) -> item1));

        chunk.getSalesFloorQtyByStoreAndSize().putAll(salesFloorQtyMap);
    }

}
