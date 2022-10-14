package com.adidas.tsar.dto.planogram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanogramByStoreDto {

    private String storeCode;
    private LocalDateTime calculatedAt;
    private List<ArticlePlanogram> articles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticlePlanogram {

        private String articleCode;
        private boolean ignoreForReverseReplenishment;
        private List<ProductPlanogram> productPlanograms;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPlanogram {
        private String gtin;
        private int priority;
        private int finalSalesFloorQty;
    }

}
