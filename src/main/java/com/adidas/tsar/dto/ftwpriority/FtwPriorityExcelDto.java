package com.adidas.tsar.dto.ftwpriority;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class FtwPriorityExcelDto {

    @ExcelRow
    private String rowNumber;

    @ExcelCellName("Brand")
    private String brand;

    @ExcelCellName("RMH GenderAge")
    private String rmhGenderAge;

    @ExcelCellName("SizeIndex")
    private String sizeIndex;

    @ExcelCellName("Priority")
    private String priority;

}
