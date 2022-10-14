package com.adidas.tsar.service;

import com.adidas.tsar.common.ExcelParser;
import com.adidas.tsar.exceptions.ImportException;
import com.adidas.tsar.exceptions.ImportFileValidationException;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class BaseExcelImportService<T, E> {

    protected static final int MAX_PROCESSING_ERRORS = 100;
    private static final List<String> supportImportFileExtensions = Lists.newArrayList(".xls", ".xlsx");
    protected String currentUser;

    public Stream<E> importFromFile(MultipartFile file, String currentUser) {
        this.currentUser = currentUser;
        validateExtension(file);
        final var parseItems = parse(file);
        log.info("Parsed {} new {} items from {} file", parseItems.size(), getParseResultClass().getSimpleName(), file.getOriginalFilename());
        validateParseItems(parseItems);
        log.info("Validation of {} file was successful", file.getOriginalFilename());
        return getImportEntities(parseItems);
    }

    abstract Class<T> getParseResultClass();

    protected void validateParseItems(List<T> parseItems) {
        if (parseItems.isEmpty()) {
            throw new ImportException("Import file is empty");
        }

        validateHeaders(parseItems.get(0));
        final var errors = parseItems.stream().skip(1)
            .flatMap(this::validateImportDto)
            .limit(MAX_PROCESSING_ERRORS)
            .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            log.warn("Validation was failed: " + errors);
            throw new ImportFileValidationException("Import failed", errors);
        }
    }

    abstract void validateHeaders(T rowItem);

    abstract Stream<String> validateImportDto(T rowDto);

    abstract Stream<E> getImportEntities(List<T> parseItems);

    protected String getErrorMessage(String header, String rowIndex, String fieldValue, String row) {
        return String.format("Incorrect %s field format: %s. Row %s, %s", header, fieldValue, rowIndex, row);
    }

    protected Optional<Integer> parseInteger(String integerString) {
        try {
            return Optional.of(Integer.parseInt(integerString));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void validateExtension(MultipartFile file) {
        if (!isSupporterFileExtension(file.getOriginalFilename())) {
            log.warn("Import file have incorrect extension: " + file.getOriginalFilename());
            throw new ImportException("Incorrect file extension. Excel file is required. File name is " + file.getName());
        }
    }

    protected boolean isSupporterFileExtension(String fileName) {
        return supportImportFileExtensions.stream().anyMatch(fileName::endsWith);
    }


    @SneakyThrows(IOException.class)
    private List<T> parse(MultipartFile file) {
        try (var fileInputStream = file.getInputStream()) {
            return new ExcelParser<>(getParseResultClass(), 1).parse(fileInputStream);
        }
    }

}