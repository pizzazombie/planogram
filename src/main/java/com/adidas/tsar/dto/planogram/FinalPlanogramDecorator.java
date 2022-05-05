package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.domain.Removal;
import com.adidas.tsar.domain.Ridred;
import com.adidas.tsar.dto.ArticleDto;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class FinalPlanogramDecorator implements MatricesByArticle {
    private static final int MIN_PRIORITY = 0;

    private final SalesFloorQtyDecorator wrapee;
    private final List<Ridred> ridReds;
    private final List<Removal> removals;
    private final Map<Matrix, Item> items;

    public FinalPlanogramDecorator(SalesFloorQtyDecorator matricesByArticle, List<Ridred> ridReds, List<Removal> removals) {
        this.wrapee = matricesByArticle;
        this.ridReds = ridReds;
        this.removals = removals;
        this.items = wrapee.getSalesFloorQtyByMatrix().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                it -> new Item(it.getValue())
            ));
    }

    @Override
    public ArticleDto getArticle() {
        return wrapee.getArticle();
    }

    @Override
    public List<Matrix> getMatrices() {
        return wrapee.getMatrices();
    }

    @Override
    public PlanogramProductType getPlanogramProductType() {
        return wrapee.getPlanogramProductType();
    }

    public int getPriority(String sizeIndex) {
        return wrapee.getPriority(sizeIndex);
    }

    @Data
    @RequiredArgsConstructor
    public static class Item {
        private final int salesFloorQty;
        private int finalSalesFloorQty;
        private int ignoreForReverseReplenishment;
    }

}
