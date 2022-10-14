package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.planogram.PlanogramByStoreDto;
import com.adidas.tsar.dto.planogram.PlanogramResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface PlanogramMapper {

    @Mapping(target = "sizeIndex", source = "planogram.sizeIndex")
    @Mapping(target = "gtin", source = "sku.gtin")
    @Mapping(target = "localSize", source = "sku.localSize")
    @Mapping(target = "modified", source = "planogram.calculatedAt")
    PlanogramResponseDto toResponse(Planogram planogram, ArticleDto.SkuResponseDto sku);

    default PlanogramByStoreDto toPlanogramByStoreDto(String storeCode, List<Planogram> storePlanograms) {
        final var planogramsByArticle = storePlanograms.stream()
            .collect(groupingBy(Planogram::getArticleCode));
        final boolean ignoreForReverseReplenishment = !storePlanograms.isEmpty() && storePlanograms.get(0).isIgnoreForReverseReplenishment();
        return new PlanogramByStoreDto(
            storeCode,
            storePlanograms.stream().min(Comparator.comparing(Planogram::getCalculatedAt)).map(Planogram::getCalculatedAt).orElse(null),
            planogramsByArticle.entrySet().stream()
                .map(entry -> new PlanogramByStoreDto.ArticlePlanogram(
                    entry.getKey(),
                    ignoreForReverseReplenishment,
                    entry.getValue().stream().map(planogram -> new PlanogramByStoreDto.ProductPlanogram(
                        planogram.getGtin(),
                        planogram.getPriority(),
                        planogram.getSalesFloorQty()
                    )).collect(toList())
                )).collect(toList())
        );
    }

}
