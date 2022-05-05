package com.adidas.tsar.common;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Parse data from an Excel file (inputStream) through map function into DTO
 * @param <S> DTO class for import data, see {@link ExcelCell}, {@link ExcelRow}
 */
@RequiredArgsConstructor
public class ExcelParser<S> {

    private final Class<S> rowPattern;
    private final int skipRows;

    public List<S> parse(InputStream inputStream) {
        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .headerCount(skipRows)
            .caseInsensitive(false)
            .preferNullOverDefault(true)
            .withCasting((fieldType, value, row, column, opt) -> value.toUpperCase(Locale.getDefault()))
            .build();

        return Poiji.fromExcel(inputStream, PoijiExcelType.XLSX, rowPattern, options);
    }

}
