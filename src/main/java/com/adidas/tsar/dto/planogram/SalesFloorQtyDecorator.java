package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class SalesFloorQtyDecorator implements MatricesByArticle {

    private final PrioritiesDecorator wrapee;
    private final int presMin;
    private final Map<Matrix, Integer> salesFloorQtyByMatrix;

    public SalesFloorQtyDecorator(PrioritiesDecorator prioritiesDecorator, int presMin) {
        this.wrapee = prioritiesDecorator;
        this.presMin = presMin;
        salesFloorQtyByMatrix = this.getMatrices().stream()
            .collect(Collectors.toMap(it -> it, it -> 0));
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

    public Integer getPriority(String sizeIndex) {
        return wrapee.getPrioritiesBySizeIndex().get(sizeIndex);
    }

    @Data
    public static class Key {
        private final String sizeIndex;
        private final String storeCode;
    }

    @Data
    public static class Item {
        private final Matrix matrix;
        private int salesFloorQty;
    }

}
