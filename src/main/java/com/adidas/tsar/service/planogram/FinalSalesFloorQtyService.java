package com.adidas.tsar.service.planogram;

import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.planogram.FinalPlanogramDecorator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinalSalesFloorQtyService {

    public void populateFinalSalesFloorQty(FinalPlanogramDecorator chunk) {
        if (!actualRidRedIsExists(chunk.getRidReds())) {
            chunk.getItems().values().forEach(item -> {
                item.setFinalSalesFloorQty(0);
                item.setIgnoreForReverseReplenishment(true);
            });
        } else {
            chunk.getItems().forEach((key, value) -> {
                if (removalExists(key.getStoreId(), chunk.getRemovals())) {
                    value.setFinalSalesFloorQty(0);
                    value.setIgnoreForReverseReplenishment(false);
                } else {
                    value.setFinalSalesFloorQty(value.getSalesFloorQty());
                    value.setIgnoreForReverseReplenishment(true);
                }
            });
        }
    }

    boolean actualRidRedIsExists(List<Ridred> ridRedsForArticle) {
        final var now = LocalDate.now();
        return ridRedsForArticle.stream()
            .anyMatch(ridred -> (
                    now.isEqual(ridred.getRed()) ||
                    now.isEqual(ridred.getRid()) ||
                    (now.isAfter(ridred.getRid()) && now.isBefore(ridred.getRed())))
            );
    }

    boolean removalExists(int storeId, List<Removal> removalsForArticle) {
        return removalsForArticle.stream()
            .anyMatch(removal -> removal.getStoreId() == storeId);
    }

}
