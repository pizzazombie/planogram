package com.adidas.tsar.dto.vmstandard;

import com.adidas.tsar.domain.VmStandard;

import javax.annotation.Nullable;

/**
 * Unique key for {@link VmStandard} entity. Used for hash-functions.
 * Consists of: Brand + RMHGenderAge + RMHCategory + RMHProductType + RMHProductDivision + SizeScale
 */
public interface VmStandardKey {

    @Nullable
    Integer getBrandId();

    @Nullable
    Integer getRmhGenderAgeId();

    @Nullable
    Integer getRmhCategoryId();

    @Nullable
    Integer getRmhProductTypeId();

    @Nullable
    Integer getRmhProductDivisionId();

    @Nullable
    Integer getSizeScaleId();

}