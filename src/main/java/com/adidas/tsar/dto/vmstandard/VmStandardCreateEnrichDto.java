package com.adidas.tsar.dto.vmstandard;

import com.adidas.tsar.dto.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VmStandardCreateEnrichDto implements VmStandardKey {

    private final VmStandardCreateDto vmStandardCreateDto;
    private final BrandDto brand;
    private final RmhGenderAgeDto rmhGenderAge;
    private final RmhCategoryDto rmhCategory;
    private final RmhProductTypeDto rmhProductType;
    private final RmhProductDivisionDto rmhProductDivision;
    private final SizeScaleDto sizeScale;

    public VmStandardCreateEnrichDto(VmStandardCreateDto vmStandardCreateDto,
                                     BrandDto brand,
                                     @Nullable RmhGenderAgeDto rmhGenderAge,
                                     @Nullable RmhCategoryDto rmhCategory,
                                     RmhProductTypeDto rmhProductType,
                                     @Nullable RmhProductDivisionDto rmhProductDivision,
                                     @Nullable SizeScaleDto sizeScale) {
        this.vmStandardCreateDto = vmStandardCreateDto;
        this.brand = brand;
        this.rmhGenderAge = rmhGenderAge;
        this.rmhCategory = rmhCategory;
        this.rmhProductType = rmhProductType;
        this.rmhProductDivision = rmhProductDivision;
        this.sizeScale = sizeScale;
    }

    @EqualsAndHashCode.Include
    @Override
    public Integer getBrandId() {
        return brand.getId();
    }

    @EqualsAndHashCode.Include
    @Nullable
    @Override
    public Integer getRmhGenderAgeId() {
        return rmhGenderAge == null ? null : rmhGenderAge.getId();
    }

    @EqualsAndHashCode.Include
    @Nullable
    @Override
    public Integer getRmhCategoryId() {
        return rmhCategory == null ? null : rmhCategory.getId();
    }

    @EqualsAndHashCode.Include
    @Override
    public Integer getRmhProductTypeId() {
        return rmhProductType.getId();
    }

    @EqualsAndHashCode.Include
    @Nullable
    @Override
    public Integer getRmhProductDivisionId() {
        return rmhProductDivision == null ? null : rmhProductDivision.getId();
    }

    @EqualsAndHashCode.Include
    @Nullable
    @Override
    public Integer getSizeScaleId() {
        return sizeScale == null ? null : sizeScale.getId();
    }
}