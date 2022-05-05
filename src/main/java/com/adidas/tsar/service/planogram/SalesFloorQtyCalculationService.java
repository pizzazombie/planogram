package com.adidas.tsar.service.planogram;

import com.adidas.tsar.dto.planogram.SalesFloorQtyDecorator;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static java.util.stream.Collectors.toList;

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
        chunk.getSalesFloorQtyByMatrix().entrySet().stream()
            .filter(entry -> entry.getKey().getQuantity() >= 0)
            .min(Comparator.comparingInt(entry -> chunk.getPriority(entry.getKey().getSizeIndex())))
            .ifPresent(entry -> entry.setValue(1));
    }

    void populateAppSalesFloorQty(SalesFloorQtyDecorator chunk) {
        final var sorted = chunk.getSalesFloorQtyByMatrix().entrySet().stream()
            .sorted(Comparator.comparingInt(pair -> chunk.getPriority(pair.getKey().getSizeIndex())))
            .collect(toList());

        int salesFloorQtyForDistributing = Math.min(
            chunk.getPresMin(),
            sorted.stream()
                .map(it -> it.getKey().getQuantity())
                .reduce(Integer::sum)
                .orElse(0));

        var iter = Iterables.cycle(sorted).iterator();
        while (salesFloorQtyForDistributing > 0) {
            final var next = iter.next();
            if (next.getKey().getQuantity() >= next.getValue() + 1) {
                next.setValue(next.getValue() + 1);
                salesFloorQtyForDistributing--;
            }
        }
    }

}
