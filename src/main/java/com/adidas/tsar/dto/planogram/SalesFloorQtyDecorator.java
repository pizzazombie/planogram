package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SalesFloorQtyDecorator implements MatricesByArticle {

    private final PrioritiesDecorator wrapee;
    private final int presMin;
    private final Map<StoreAndSizeKey, SalesFloorQtyItem> salesFloorQtyByStoreAndSize;

    public SalesFloorQtyDecorator(PrioritiesDecorator prioritiesDecorator, int presMin) {
        this.wrapee = prioritiesDecorator;
        this.presMin = presMin;
        this.salesFloorQtyByStoreAndSize = new HashMap<>(this.getMatrices().size());
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
}
