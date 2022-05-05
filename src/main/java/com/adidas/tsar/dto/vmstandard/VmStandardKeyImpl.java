package com.adidas.tsar.dto.vmstandard;

import com.adidas.tsar.domain.VmStandard;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class VmStandardKeyImpl implements VmStandardKey {

    private Integer brandId;
    private Integer rmhGenderAgeId;
    private Integer rmhCategoryId;
    private Integer rmhProductTypeId;
    private Integer rmhProductDivisionId;
    private Integer sizeScaleId;

    public VmStandardKeyImpl(VmStandard vmStandard) {
        this.brandId = vmStandard.getBrandId();
        this.rmhGenderAgeId = vmStandard.getRmhGenderAgeId();
        this.rmhCategoryId = vmStandard.getRmhCategoryId();
        this.rmhProductTypeId = vmStandard.getRmhProductTypeId();
        this.rmhProductDivisionId = vmStandard.getRmhProductDivisionId();
        this.sizeScaleId = vmStandard.getSizeScaleId();
    }

}
