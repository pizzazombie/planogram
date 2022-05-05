package com.adidas.tsar.dto.vmstandard;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public final class VmStandardExcelDto {

    @ExcelRow
    private String rowNumber;

    @ExcelCellName("Brand")
    private String brand;

    @ExcelCellName("RMH GenderAge")
    private String rmhGenderAge;

    @ExcelCellName("RMH Category")
    private String rmhCategory;

    @ExcelCellName("RMH ProductType")
    private String rmhProductType;

    @ExcelCellName("RMH ProductDivision")
    private String rmhProductDivision;

    @ExcelCellName("SizeScale")
    private String sizeScale;

    @ExcelCellName("PresMin")
    private String presMin;

}
