package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import com.adidas.tsar.dto.ArticleDto;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class PrioritiesDecorator implements MatricesByArticle {
    private static final int MIN_PRIORITY = 0;
    private final MatricesByArticle wrapee;
    private final Map<String, Integer> quantitySumBySizeIndex;
    private final Map<String, Integer> prioritiesBySizeIndex;

    public PrioritiesDecorator(MatricesByArticle articleInfo) {
        this.wrapee = articleInfo;
        this.quantitySumBySizeIndex = articleInfo.getMatrices().stream().collect(Collectors.groupingBy(Matrix::getSizeIndex, Collectors.summingInt(Matrix::getQuantity)));
        this.prioritiesBySizeIndex = getSizeIndexes().stream().collect(Collectors.toMap(Function.identity(), it -> MIN_PRIORITY));
    }

    public Set<String> getSizeIndexes() {
        return quantitySumBySizeIndex.keySet();
    }

    public void setPriority(String sizeIndex, Integer priority) {
            prioritiesBySizeIndex.put(sizeIndex, priority);
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
}
