package com.adidas.tsar.dto.planogram;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesFloorQtyItem {
    private int salesFloorQty;
    private int finalSalesFloorQty;
    private boolean ignoreForReverseReplenishment;
}
