package com.adidas.tsar.common.export;

import com.adidas.tsar.exceptions.AppException;
import com.google.common.base.Strings;
import com.poiji.annotation.ExcelCellName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnnotationXlsxExporter<T> {

    public InputStream export(Collection<T> data, ExportOptions<T> options) {
        final var exportDefinition = getExportDefinition(options.getDtoClass());
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(options.getSectionName());
            appendHeader(sheet, exportDefinition);
            for (T dto : data) {
                appendRow(sheet, dto, exportDefinition);
            }
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            } catch (IOException e) {
                log.warn("Unable to make an import: section = {}", options.getSectionName(), e);
                throw new AppException("Unable to create report file");
            }
        } catch (AppException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new AppException("", e.getMessage());
        }
    }

    private void appendHeader(Sheet sheet, List<ExportFieldDefinition> exportDefinition) {
        CellStyle cellStyle = getDefaultHeaderStyle(sheet.getWorkbook());
        Row row = appendNewRow(sheet, 0);
        exportDefinition.forEach(definition -> appendCell(row, definition.getIndex(), definition.getTitle(), cellStyle));
    }

    private void appendRow(Sheet sheet, T dto, List<ExportFieldDefinition> exportDefinition) {
        Row row = appendNewRow(sheet);
        exportDefinition.forEach(definition -> appendCell(row, definition.getIndex(), getFieldValue(dto, definition.getField()).map(Object::toString).orElse("")));
    }

    private Row appendNewRow(Sheet sheet, int index) {
        return sheet.createRow(index);
    }

    private Row appendNewRow(Sheet sheet) {
        return sheet.createRow(sheet.getLastRowNum() + 1);
    }

    private Cell appendCell(Row row, Integer columnIndex, @Nullable String value, CellStyle cellStyle) {
        Cell cell = appendCell(row, columnIndex, value);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    private Cell appendCell(Row row, Integer columnIndex, @Nullable String value) {
        Cell cell = row.createCell(columnIndex);
        if (!Strings.isNullOrEmpty(value)) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue("");
        }
        return cell;
    }

    private CellStyle getDefaultHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private List<ExportFieldDefinition> getExportDefinition(Class<T> exportClass) {
        final var fields = Arrays.stream(exportClass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(ExcelCellName.class))
            .collect(Collectors.toList());

        List<ExportFieldDefinition> result = new ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            result.add(new ExportFieldDefinition(i, field.getAnnotation(ExcelCellName.class).value(), field));
        }
        return result;
    }

    @SneakyThrows(value = IllegalAccessException.class)
    private Optional<Object> getFieldValue(T dto, Field field) {
        field.setAccessible(true);
        return Optional.ofNullable(field.get(dto));
    }

    @RequiredArgsConstructor
    @Getter
    static class ExportFieldDefinition {
        private final int index;
        private final String title;
        private final Field field;
    }

}