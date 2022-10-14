package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.domain.Matrix;
import lombok.Data;

@Data
public class StoreAndSizeKey {
    private final Integer storeId;
    private final String sizeIndex;

    public StoreAndSizeKey(Matrix matrix) {
        this.storeId = matrix.getStoreId();
        this.sizeIndex = matrix.getSizeIndex();
    }
}
