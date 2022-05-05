package com.adidas.tsar.service;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.common.export.AnnotationXlsxExporter;
import com.adidas.tsar.common.export.ExportOptions;
import com.adidas.tsar.data.VmStandardRepository;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhCategoryDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.RmhProductDivisionDto;
import com.adidas.tsar.dto.RmhProductTypeDto;
import com.adidas.tsar.dto.SizeScaleDto;
import com.adidas.tsar.dto.vmstandard.VmStandardCreateDto;
import com.adidas.tsar.dto.vmstandard.VmStandardCreateEnrichDto;
import com.adidas.tsar.dto.vmstandard.VmStandardExcelDto;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.dto.vmstandard.VmStandardResponse;
import com.adidas.tsar.exceptions.AppException;
import com.adidas.tsar.exceptions.EntityNotFoundException;
import com.adidas.tsar.mapper.VmStandardFactory;
import com.adidas.tsar.mapper.VmStandardMapper;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmStandardService {

    private static final String CREATE_VM_STANDARD_FLOW_TITLE = "Loading the VM Standard List";
    private static final String EXPORT_SECTION_TITLE = "Export VM Standards";
    private static final String EDIT_SECTION_TITLE = "Edit VM Standard";

    private final VmStandardMapper standardMapper;
    private final TsarMasterDataApiClient tsarMasterDataApiClient;
    private final VmStandardRepository vmStandardRepository;
    private final AnnotationXlsxExporter<VmStandardExcelDto> excelExporter;

    @Value("${app.export.standard-name-pattern}")
    private String exportNamePattern;

    public Page<VmStandardResponse> findVmStandards(Pageable paging) {
        final var dictionaries = buildDictionaries();
        final var vmStandards = vmStandardRepository.findAll(paging);
        return vmStandards.map(it -> standardMapper.toResponse(
            it,
            dictionaries.getDictionaryItem(BrandDto.class, it.getBrandId()).orElse(null),
            dictionaries.getDictionaryItem(RmhGenderAgeDto.class, it.getRmhGenderAgeId()).orElse(null),
            dictionaries.getDictionaryItem(RmhCategoryDto.class, it.getRmhCategoryId()).orElse(null),
            dictionaries.getDictionaryItem(RmhProductTypeDto.class, it.getRmhProductTypeId()).orElse(null),
            dictionaries.getDictionaryItem(RmhProductDivisionDto.class, it.getRmhProductDivisionId()).orElse(null),
            dictionaries.getDictionaryItem(SizeScaleDto.class, it.getSizeScaleId()).orElse(null)

        ));
    }

    public void createVmStandards(List<VmStandardCreateDto> requestBody, String currentUser) {
        final var dictionaries = buildDictionaries();
        Set<VmStandardKey> newItems = new HashSet<>();
        requestBody.stream()
            .map(createDto ->
                new VmStandardCreateEnrichDto(
                    createDto,
                    dictionaries.getOrThrow(BrandDto.class, createDto.getBrand(), true, CREATE_VM_STANDARD_FLOW_TITLE),
                    dictionaries.getOrThrow(RmhGenderAgeDto.class, createDto.getRmhGenderAge(), false, CREATE_VM_STANDARD_FLOW_TITLE),
                    dictionaries.getOrThrow(RmhCategoryDto.class, createDto.getRmhCategory(), false, CREATE_VM_STANDARD_FLOW_TITLE),
                    dictionaries.getOrThrow(RmhProductTypeDto.class, createDto.getRmhProductType(), true, CREATE_VM_STANDARD_FLOW_TITLE),
                    dictionaries.getOrThrow(RmhProductDivisionDto.class, createDto.getRmhProductDivision(), false, CREATE_VM_STANDARD_FLOW_TITLE),
                    dictionaries.getOrThrow(SizeScaleDto.class, createDto.getSizeScale(), false, CREATE_VM_STANDARD_FLOW_TITLE)
                )
            ).forEach(key -> {
            if (!newItems.add(key)) {
                throw new AppException(CREATE_VM_STANDARD_FLOW_TITLE, "Input have duplicate values: " + key);
            }
        });

        final var errors = vmStandardRepository.findAllByKeys(newItems).stream()
            .map(vmStandard -> String.format("VM Standard %s already exists", vmStandard))
            .limit(100)
            .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new AppException(CREATE_VM_STANDARD_FLOW_TITLE, "Some VM Standards already exists", errors);
        }

        var newVmStandards = newItems.stream()
            .map(vmStandardKey -> {
                VmStandardCreateEnrichDto dto = (VmStandardCreateEnrichDto) vmStandardKey;
                return VmStandardFactory.getVmStandard(
                    dto.getBrand(),
                    dto.getRmhGenderAge(),
                    dto.getRmhCategory(),
                    dto.getRmhProductType(),
                    dto.getRmhProductDivision(),
                    dto.getSizeScale(),
                    dto.getVmStandardCreateDto().getPresMin(),
                    currentUser);
            }).collect(Collectors.toList());

        vmStandardRepository.saveAll(newVmStandards);
    }

    public void updatePresMin(long standardId, Integer presMin, String currentUser) {
        final var vmStandard = vmStandardRepository.findById(standardId).orElseThrow(() -> new EntityNotFoundException(EDIT_SECTION_TITLE, "VM Standard is not found"));
        vmStandard.changePresMin(presMin, currentUser);
        vmStandardRepository.save(vmStandard);
    }

    public void deleteVmStandards(List<Long> ids) {
        log.info("Delete VM Standards by ids: {}", ids);
        vmStandardRepository.deleteAllById(ids);
    }

    public void importFromExcel(MultipartFile file, String currentUser) {
        final var importedStandards = new VmStandardsImportService(buildDictionaries()).importFromFile(file, currentUser).stream()
            .collect(Collectors.toMap(VmStandard::buildKey, it -> it));
        final var existingStandards = vmStandardRepository.findAllByKeys(importedStandards.keySet()).stream()
            .collect(Collectors.toMap(VmStandard::buildKey, it -> it));

        final var mergedEntities = importedStandards.entrySet().stream()
            .map(entry -> {
                var existingItem = Optional.ofNullable(existingStandards.get(entry.getKey()));
                return existingItem.map(it -> {
                    it.changePresMin(entry.getValue().getPresMin(), currentUser);
                    return it;
                }).orElse(entry.getValue());
            })
            .collect(Collectors.toList());

        vmStandardRepository.saveAll(mergedEntities);
    }

    public InputStream exportToExcel() {
        final var dictionaries = buildDictionaries();
        return excelExporter.export(
            vmStandardRepository.findAll().stream()
                .map(vmStandard -> standardMapper.toExcelDto(
                    vmStandard,
                    dictionaries.getDictionaryItem(BrandDto.class, vmStandard.getBrandId()).orElse(null),
                    dictionaries.getDictionaryItem(RmhGenderAgeDto.class, vmStandard.getRmhGenderAgeId()).orElse(null),
                    dictionaries.getDictionaryItem(RmhCategoryDto.class, vmStandard.getRmhCategoryId()).orElse(null),
                    dictionaries.getDictionaryItem(RmhProductTypeDto.class, vmStandard.getRmhProductTypeId()).orElse(null),
                    dictionaries.getDictionaryItem(RmhProductDivisionDto.class, vmStandard.getRmhProductDivisionId()).orElse(null),
                    dictionaries.getDictionaryItem(SizeScaleDto.class, vmStandard.getSizeScaleId()).orElse(null)
                ))
                .collect(Collectors.toList()),
            new ExportOptions<>(VmStandardExcelDto.class, EXPORT_SECTION_TITLE)
        );
    }

    public String buildExportFileName(LocalDateTime time) {
        return String.format(exportNamePattern, time.format(ISO_LOCAL_DATE_TIME));
    }

    public long countOfStandards() {
        return vmStandardRepository.count();
    }

    private DictionariesCollectionUtils buildDictionaries() {
        return new DictionariesCollectionUtils(List.of(
            Pair.of(BrandDto.class, tsarMasterDataApiClient.getBrands().getData()),
            Pair.of(RmhGenderAgeDto.class, tsarMasterDataApiClient.getRmhGenderAges().getData()),
            Pair.of(RmhCategoryDto.class, tsarMasterDataApiClient.getRmhCategories().getData()),
            Pair.of(RmhProductTypeDto.class, tsarMasterDataApiClient.getRmhProductTypes().getData()),
            Pair.of(RmhProductDivisionDto.class, tsarMasterDataApiClient.getRmhProductDivisions().getData()),
            Pair.of(SizeScaleDto.class, tsarMasterDataApiClient.getSizeScales().getData())
        ));
    }

}
