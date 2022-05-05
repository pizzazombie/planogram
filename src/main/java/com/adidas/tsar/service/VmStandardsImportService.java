package com.adidas.tsar.service;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.dto.vmstandard.VmStandardExcelDto;
import com.adidas.tsar.exceptions.ImportException;
import com.adidas.tsar.mapper.VmStandardFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
public class VmStandardsImportService extends BaseExcelImportService<VmStandardExcelDto, VmStandard> {

    private static final String BRAND_HEADER = "Brand";
    private static final String RMH_GENDER_AGE_HEADER = "RMH GenderAge";
    private static final String RMH_CATEGORY_HEADER = "RMH Category";
    private static final String RMH_PRODUCT_TYPE_HEADER = "RMH ProductType";
    private static final String RMH_PRODUCT_DIVISION_HEADER = "RMH ProductDivision";
    private static final String SIZE_SCALE_HEADER = "SizeScale";
    private static final String PRES_MIN_HEADER = "PresMin";
    private static final String IMPORT_FTW_PRIORITY_FLOW_TITLE = "Import the VM Standard List";
    private final Pattern sizeScalePattern = Pattern.compile("^[a-zA-Z0-9]{2}$");
    private final DictionariesCollectionUtils dictionaries;

    public VmStandardsImportService(DictionariesCollectionUtils dictionaries) {
        this.dictionaries = dictionaries;
    }

    @Override
    Class<VmStandardExcelDto> getParseResultClass() {
        return VmStandardExcelDto.class;
    }

    @Override
    List<VmStandard> getImportEntities(List<VmStandardExcelDto> parseItems) {
        return parseItems.stream()
            .map(createVmStandardDto -> VmStandardFactory.getVmStandard(
                dictionaries.getOrThrow(BrandDto.class, createVmStandardDto.getBrand(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(RmhGenderAgeDto.class, createVmStandardDto.getRmhGenderAge(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(RmhCategoryDto.class, createVmStandardDto.getRmhCategory(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(RmhProductTypeDto.class, createVmStandardDto.getRmhProductType(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(RmhProductDivisionDto.class, createVmStandardDto.getRmhProductDivision(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(SizeScaleDto.class, createVmStandardDto.getSizeScale(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                    parseInteger(createVmStandardDto.getPresMin()).orElseThrow(() -> new IllegalArgumentException(getErrorMessage(PRES_MIN_HEADER, createVmStandardDto.getPresMin(), createVmStandardDto))),
                currentUser
            )).collect(Collectors.toList());
    }

    @Override
    void validateHeaders(VmStandardExcelDto dto) {
        if (Strings.isNullOrEmpty(dto.getBrand())) throw new ImportException(buildHeaderErrorMessage(BRAND_HEADER));
        if (Strings.isNullOrEmpty(dto.getRmhGenderAge()))
            throw new ImportException(buildHeaderErrorMessage(RMH_GENDER_AGE_HEADER));
        if (Strings.isNullOrEmpty(dto.getRmhCategory()))
            throw new ImportException(buildHeaderErrorMessage(RMH_CATEGORY_HEADER));
        if (Strings.isNullOrEmpty(dto.getRmhProductType()))
            throw new ImportException(buildHeaderErrorMessage(RMH_PRODUCT_TYPE_HEADER));
        if (Strings.isNullOrEmpty(dto.getRmhProductDivision()))
            throw new ImportException(buildHeaderErrorMessage(RMH_PRODUCT_DIVISION_HEADER));
        if (Strings.isNullOrEmpty(dto.getSizeScale()))
            throw new ImportException(buildHeaderErrorMessage(SIZE_SCALE_HEADER));
        if (Strings.isNullOrEmpty(dto.getPresMin()))
            throw new ImportException(buildHeaderErrorMessage(PRES_MIN_HEADER));
    }

    @Override
    List<String> validateImportDto(VmStandardExcelDto rowDto) {
        return Lists.newArrayList(
            validateDictionaryValue(BrandDto.class, rowDto, VmStandardExcelDto::getBrand, true, BRAND_HEADER),
            validateDictionaryValue(RmhGenderAgeDto.class, rowDto, VmStandardExcelDto::getRmhGenderAge, false, RMH_GENDER_AGE_HEADER),
            validateDictionaryValue(RmhCategoryDto.class, rowDto, VmStandardExcelDto::getRmhCategory, false, RMH_CATEGORY_HEADER),
            validateDictionaryValue(RmhProductTypeDto.class, rowDto, VmStandardExcelDto::getRmhProductType, true, RMH_PRODUCT_TYPE_HEADER),
            validateDictionaryValue(RmhProductDivisionDto.class, rowDto, VmStandardExcelDto::getRmhProductDivision, false, RMH_PRODUCT_DIVISION_HEADER),
            validateSizeScaleValue(rowDto),
            validatePresMinValue(rowDto)
        ).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private <T> String validateDictionaryValue(Class<T> clazz, VmStandardExcelDto rowDto, Function<VmStandardExcelDto, String> getValueFunc, boolean isRequired, String headerName) {
        final var value = getValueFunc.apply(rowDto);
        if ((isRequired || !Strings.isNullOrEmpty(value)) && dictionaries.getDictionaryItem(clazz, value).isEmpty()) {
            return getErrorMessage(headerName, value, rowDto);
        }
        return null;
    }

    private String validateSizeScaleValue(VmStandardExcelDto rowDto) {
        if (!Strings.isNullOrEmpty(rowDto.getSizeScale()) && !sizeScalePattern.matcher(rowDto.getSizeScale()).matches())
            return getErrorMessage(SIZE_SCALE_HEADER, rowDto.getSizeScale(), rowDto);
        else
            return null;
    }

    private String validatePresMinValue(VmStandardExcelDto rowDto) {
        if (parseInteger(rowDto.getPresMin()).isEmpty())
            return getErrorMessage(PRES_MIN_HEADER, rowDto.getSizeScale(), rowDto);
        else
            return null;
    }

    private String buildHeaderErrorMessage(String wrongHeaderName) {
        return String.format("Incorrect headers %s. Header or import value is not specified", wrongHeaderName);
    }

    private String getErrorMessage(String header, String value, VmStandardExcelDto rowDto) {
        String rowFormatString = String.format("%s|%s|%s|%s|%s|%s|%s",
            rowDto.getBrand(),
            rowDto.getRmhGenderAge(),
            rowDto.getRmhCategory(),
            rowDto.getRmhProductType(),
            rowDto.getRmhProductDivision(),
            rowDto.getSizeScale(),
            rowDto.getPresMin()
        );
        return getErrorMessage(header, rowDto.getRowNumber(), value, rowFormatString);
    }

}
