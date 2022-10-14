package com.adidas.tsar.service;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.DictionaryEntity;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityExcelDto;
import com.adidas.tsar.exceptions.ImportException;
import com.adidas.tsar.mapper.FtwPriorityFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@Slf4j
public class FtwPrioritiesImportService extends BaseExcelImportService<FtwPriorityExcelDto, FtwPriority> {

    private static final String BRAND_HEADER = "Brand";
    private static final String RMH_GENDER_AGE_HEADER = "RMH GenderAge";
    private static final String SIZE_INDEX_HEADER = "SizeIndex";
    private static final String PRIORITY_HEADER = "Priority";
    private final String IMPORT_FTW_PRIORITY_FLOW_TITLE = "Import the FTW Priority List";
    private final Pattern sizeIndexPattern = Pattern.compile("^[0-9]{3}$");
    private final DictionariesCollectionUtils dictionaries;

    public FtwPrioritiesImportService(DictionariesCollectionUtils dictionaries) {
        this.dictionaries = dictionaries;
    }

    @Override
    Class<FtwPriorityExcelDto> getParseResultClass() {
        return FtwPriorityExcelDto.class;
    }

    @Override
    Stream<FtwPriority> getImportEntities(List<FtwPriorityExcelDto> parseItems) {
        return parseItems.stream()
            .map(createFtwPriorityDto -> FtwPriorityFactory.getFtwPriority(
                dictionaries.getOrThrow(BrandDto.class, createFtwPriorityDto.getBrand(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                dictionaries.getOrThrow(RmhGenderAgeDto.class, createFtwPriorityDto.getRmhGenderAge(), IMPORT_FTW_PRIORITY_FLOW_TITLE),
                createFtwPriorityDto.getSizeIndex(),
                parseInteger(createFtwPriorityDto.getPriority())
                    .orElseThrow(() -> new IllegalArgumentException(getErrorMessage(PRIORITY_HEADER, createFtwPriorityDto.getPriority(), createFtwPriorityDto))),
                this.currentUser
            ));
    }

    @Override
    void validateHeaders(FtwPriorityExcelDto dto) {
        if (Strings.isNullOrEmpty(dto.getBrand())) throw new ImportException(buildHeaderErrorMessage(BRAND_HEADER));
        if (Strings.isNullOrEmpty(dto.getRmhGenderAge()))
            throw new ImportException(buildHeaderErrorMessage(RMH_GENDER_AGE_HEADER));
        if (Strings.isNullOrEmpty(dto.getSizeIndex()))
            throw new ImportException(buildHeaderErrorMessage(SIZE_INDEX_HEADER));
        if (Strings.isNullOrEmpty(dto.getPriority()))
            throw new ImportException(buildHeaderErrorMessage(PRIORITY_HEADER));
    }

    @Override
    Stream<String> validateImportDto(FtwPriorityExcelDto rowDto) {
        return Lists.newArrayList(
            validateDictionaryValue(BrandDto.class, rowDto, FtwPriorityExcelDto::getBrand, true, BRAND_HEADER),
            validateDictionaryValue(RmhGenderAgeDto.class, rowDto, FtwPriorityExcelDto::getRmhGenderAge, true, RMH_GENDER_AGE_HEADER),
            validateSizeIndex(rowDto),
            validatePriorityValue(rowDto)
        ).stream().filter(Objects::nonNull);
    }

    private <T extends DictionaryEntity> String validateDictionaryValue(Class<T> clazz, FtwPriorityExcelDto rowDto, Function<FtwPriorityExcelDto, String> getValueFunc, boolean isRequired, String headerName) {
        final var value = getValueFunc.apply(rowDto);
        if ((isRequired || !Strings.isNullOrEmpty(value)) && dictionaries.getDictionaryItem(clazz, value).isEmpty()) {
            return getErrorMessage(headerName, value, rowDto);
        }
        return null;
    }

    private String validateSizeIndex(FtwPriorityExcelDto rowDto) {
        if (Strings.isNullOrEmpty(rowDto.getSizeIndex()) || !sizeIndexPattern.matcher(rowDto.getSizeIndex()).matches())
            return getErrorMessage(SIZE_INDEX_HEADER, rowDto.getSizeIndex(), rowDto);
        else
            return null;
    }

    private String validatePriorityValue(FtwPriorityExcelDto rowDto) {
        if (parseInteger(rowDto.getPriority()).isEmpty())
            return getErrorMessage(PRIORITY_HEADER, rowDto.getPriority(), rowDto);
        else
            return null;
    }

    private String buildHeaderErrorMessage(String wrongHeaderName) {
        return String.format("Incorrect headers %s. Header or import value is not specified", wrongHeaderName);
    }

    private String getErrorMessage(String header, String value, FtwPriorityExcelDto rowDto) {
        String rowFormatString = String.format("%s|%s|%s|%s", rowDto.getBrand(), rowDto.getRmhGenderAge(), rowDto.getSizeIndex(), rowDto.getPriority());
        return getErrorMessage(header, rowDto.getRowNumber(), value, rowFormatString);
    }

}
