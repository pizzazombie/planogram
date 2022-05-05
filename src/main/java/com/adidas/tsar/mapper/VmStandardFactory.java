package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.*;
import com.adidas.tsar.dto.*;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;

@UtilityClass
public class VmStandardFactory {

    public VmStandard getVmStandard(BrandDto brand,
                                           @Nullable RmhGenderAgeDto rmhGenderAge,
                                           @Nullable RmhCategoryDto category,
                                           RmhProductTypeDto productType,
                                           @Nullable RmhProductDivisionDto productDivision,
                                           @Nullable SizeScaleDto sizeScale,
                                           @Nullable Integer presMin,
                                           String user) {
        VmStandard vmStandard = new VmStandard()
            .setBrandId(brand.getId())
            .setRmhGenderAgeId(rmhGenderAge != null ? rmhGenderAge.getId() : null)
            .setRmhCategoryId(category != null ? category.getId() : null)
            .setRmhProductTypeId(productType.getId())
            .setRmhProductDivisionId(productDivision != null ? productDivision.getId() : null)
            .setSizeScaleId(sizeScale != null ? sizeScale.getId() : null);
        vmStandard.changePresMin(presMin, user);
        return vmStandard;

    }

}
