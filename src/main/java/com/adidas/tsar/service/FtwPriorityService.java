package com.adidas.tsar.service;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.common.export.AnnotationXlsxExporter;
import com.adidas.tsar.common.export.ExportOptions;
import com.adidas.tsar.data.FtwPriorityRepository;
import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityCreateDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityExcelDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKey;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityResponse;
import com.adidas.tsar.exceptions.AppException;
import com.adidas.tsar.exceptions.EntityNotFoundException;
import com.adidas.tsar.mapper.FtwPriorityMapper;
import com.adidas.tsar.rest.feign.TsarMasterDataApiClient;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
public class FtwPriorityService {

    private final String CREATE_FTW_PRIORITY_FLOW_TITLE = "Loading the FTW Priority List";
    private static final String EXPORT_SECTION_TITLE = "Export FTW Priorities";
    private static final String EDIT_SECTION_TITLE = "Edit FTW Priority";

    private final TsarMasterDataApiClient tsarMasterDataApiClient;
    private final FtwPriorityRepository ftwPriorityRepository;
    private final FtwPriorityMapper ftwPriorityMapper;
    private final AnnotationXlsxExporter<FtwPriorityExcelDto> excelExporter;

    @Value("${app.export.priority-name-pattern}")
    private String exportNamePattern;

    public Page<FtwPriorityResponse> findFtwPriorities(Pageable paging) {
        final var dictionaries = buildDictionaries();
        final var ftwPriorities = ftwPriorityRepository.findAll(paging);
        return ftwPriorities.map(it -> ftwPriorityMapper.toResponse(
            it,
            dictionaries.getDictionaryItem(BrandDto.class, it.getBrandId()).orElse(null),
            dictionaries.getDictionaryItem(RmhGenderAgeDto.class, it.getRmhGenderAgeId()).orElse(null)
        ));
    }

    public void deleteFtwPriorities(List<Long> ids) {
        log.info("Delete FTW Priorities by ids: {}", ids);
        ftwPriorityRepository.deleteAllById(ids);
    }

    public void createFtwPriorities(List<FtwPriorityCreateDto> requestBody, String user) {
        log.info("Creating new FTW Priorities: {}", requestBody);
        final var dictionaries = buildDictionaries();

        Set<FtwPriorityKey> newItems = new HashSet<>();
        requestBody.stream().map(ftwPriorityCreateDto -> new CreateFtwPriorityEnrichDto(
            ftwPriorityCreateDto,
            dictionaries.getOrThrow(BrandDto.class, ftwPriorityCreateDto.getBrand(), CREATE_FTW_PRIORITY_FLOW_TITLE),
            dictionaries.getOrThrow(RmhGenderAgeDto.class, ftwPriorityCreateDto.getRmhGenderAge(), CREATE_FTW_PRIORITY_FLOW_TITLE)
        )).forEach(key -> {
            if (!newItems.add(key)) {
                throw new AppException(CREATE_FTW_PRIORITY_FLOW_TITLE, "Input have duplicate values: " + key);
            }
        });

        final var errors = ftwPriorityRepository.findAllByKeys(newItems).stream()
            .map(ftwPriority -> String.format("FTW Priority %s already exists", ftwPriority))
            .limit(100)
            .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            log.warn("Creating FTW Priorities failed: {}", errors);
            throw new AppException(CREATE_FTW_PRIORITY_FLOW_TITLE, "Some FTW Priorities already exists", errors);
        }

        var newFtwPriorities = newItems.stream()
            .map(ftwPriorityKey -> {
                CreateFtwPriorityEnrichDto dto = (CreateFtwPriorityEnrichDto) ftwPriorityKey;
                return ftwPriorityMapper.getFtwPriority(dto.getBrand(), dto.getRmhGenderAge(), dto.getSizeIndex(), dto.ftwPriorityCreateDto.getPriority(), user);
            }).collect(Collectors.toList());
        log.info("Creating new FTW Priorities: {}", newFtwPriorities);
        ftwPriorityRepository.saveAll(newFtwPriorities);
    }

    public void updatePriority(long ftwPriorityId, Integer priority, String currentUser) {
        final var ftwPriority = ftwPriorityRepository.findById(ftwPriorityId).orElseThrow(() -> new EntityNotFoundException(EDIT_SECTION_TITLE, "FTW Priority not found"));
        ftwPriority.changePriority(priority, currentUser);
        ftwPriorityRepository.save(ftwPriority);
    }

    public void importFromExcel(MultipartFile file, String user) {
        log.info("Importing new FTW Priorities from {}", file.getOriginalFilename());
        final var dictionaries = buildDictionaries();
        final var importedFtwPrioritiesMap = new FtwPrioritiesImportService(dictionaries)
            .importFromFile(file, user).stream()
            .collect(Collectors.toMap(FtwPriority::buildKey, it -> it));
        final var existingFtwPrioritiesMap = ftwPriorityRepository.findAllByKeys(importedFtwPrioritiesMap.keySet()).stream()
            .collect(Collectors.toMap(FtwPriority::buildKey, it -> it));
        log.info("Some FTW Priorities {} already exists, they are will be changed, ids:{}",
            existingFtwPrioritiesMap.keySet().size(),
            existingFtwPrioritiesMap.values().stream().map(FtwPriority::getId).collect(Collectors.toList())
        );

        final var mergedEntities = importedFtwPrioritiesMap.entrySet().stream()
            .map(entry -> {
                var existingItem = Optional.ofNullable(existingFtwPrioritiesMap.get(entry.getKey()));
                return existingItem.map(it -> {
                    it.changePriority(entry.getValue().getPriority(), user);
                    return it;
                }).orElse(entry.getValue());
            })
            .collect(Collectors.toList());

        log.info("Saving new imported FTW Priorities: {}", mergedEntities);
        ftwPriorityRepository.saveAll(mergedEntities);
    }

    public InputStream exportToExcel() {
        final var dictionaries = buildDictionaries();
        return excelExporter.export(
            ftwPriorityRepository.findAll().stream()
                .map(ftwPriority -> ftwPriorityMapper.toExportDto(
                    ftwPriority,
                    dictionaries.getDictionaryItem(BrandDto.class, ftwPriority.getBrandId()).orElse(null),
                    dictionaries.getDictionaryItem(RmhGenderAgeDto.class, ftwPriority.getRmhGenderAgeId()).orElse(null)
                ))
                .collect(Collectors.toList()),
            new ExportOptions<>(FtwPriorityExcelDto.class, EXPORT_SECTION_TITLE)
        );
    }

    public String buildExportFileName(LocalDateTime time) {
        return String.format(exportNamePattern, time.format(ISO_LOCAL_DATE_TIME));
    }

    public long countOfPriorities() {
        return ftwPriorityRepository.count();
    }

    private DictionariesCollectionUtils buildDictionaries() {
        return new DictionariesCollectionUtils(List.of(
            Pair.of(BrandDto.class, tsarMasterDataApiClient.getBrands().getData()),
            Pair.of(RmhGenderAgeDto.class, tsarMasterDataApiClient.getRmhGenderAges().getData())
        ));
    }

    @Data
    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public static class CreateFtwPriorityEnrichDto implements FtwPriorityKey {

        private final FtwPriorityCreateDto ftwPriorityCreateDto;
        private final BrandDto brand;
        private final RmhGenderAgeDto rmhGenderAge;

        @EqualsAndHashCode.Include
        @Override
        public int getBrandId() {
            return brand.getId();
        }

        @EqualsAndHashCode.Include
        @Override
        public int getRmhGenderAgeId() {
            return rmhGenderAge.getId();
        }

        @EqualsAndHashCode.Include
        @Override
        public String getSizeIndex() {
            return ftwPriorityCreateDto.getSizeIndex();
        }

    }

}
