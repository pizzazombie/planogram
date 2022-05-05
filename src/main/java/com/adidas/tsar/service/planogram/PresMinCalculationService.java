package com.adidas.tsar.service.planogram;

import com.adidas.tsar.common.DictionariesCollectionUtils;
import com.adidas.tsar.data.VmStandardRepository;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.dto.planogram.MatricesByArticleImpl;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.dto.vmstandard.VmStandardKeyImpl;
import com.adidas.tsar.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PresMinCalculationService {

    @Value("${app.empty-dictionary-item-id}")
    private int emptyDictionaryItemId;

    private final int FOOTWEAR_PRES_MIN = 1;
    private final int MIN_PRES_MIN = 0;
    private final VmStandardRepository standardRepository;

    public Map<VmStandardKey, Integer> preparePresMinMap() {
        return standardRepository.findAll().stream()
            .collect(toMap(VmStandard::buildKey, VmStandard::getPresMin, (presMin1, presMin2) -> presMin1));
    }

    public int getPresMin(Map<VmStandardKey, Integer> presMinMap, DictionariesCollectionUtils dictionaries, MatricesByArticleImpl batch) {
        switch (batch.getPlanogramProductType()) {
            case FOOTWEAR:
                return FOOTWEAR_PRES_MIN;
            case APPAREL:
                return getApparelPresMin(presMinMap, dictionaries, batch.getArticle());
            default:
                throw new AppException("Planogram Caclulating", "Unknown product type: " + batch.getPlanogramProductType().name());
        }
    }

    public int getApparelPresMin(Map<VmStandardKey, Integer> presMinMap, DictionariesCollectionUtils dictionaries, ArticleDto articleDto) {
        Integer productTypeId = dictionaries.getDictionaryItem(RmhProductTypeDto.class, articleDto.getProductType()).map(RmhProductTypeDto::getId).orElse(null);
        Integer divisionId = dictionaries.getDictionaryItem(RmhProductDivisionDto.class, articleDto.getProductDivision()).map(RmhProductDivisionDto::getId).orElse(null);
        Integer brandId = dictionaries.getDictionaryItem(BrandDto.class, articleDto.getBrand()).map(BrandDto::getId).orElse(null);
        Integer ageId = dictionaries.getDictionaryItem(RmhGenderAgeDto.class, articleDto.getGenderAge()).map(RmhGenderAgeDto::getId).orElse(null);
        Integer categoryId = dictionaries.getDictionaryItem(RmhCategoryDto.class, articleDto.getCategory()).map(RmhCategoryDto::getId).orElse(null);
        Integer sizeScaleId = dictionaries.getDictionaryItem(SizeScaleDto.class, articleDto.getSizeScale()).map(SizeScaleDto::getId).orElse(null);

        VmStandardKeyImpl key = new VmStandardKeyImpl(brandId, ageId, categoryId, productTypeId, divisionId, sizeScaleId);
        return Optional.ofNullable(presMinMap.get(key))
            .or(() -> Optional.ofNullable(presMinMap.get(key.setSizeScaleId(emptyDictionaryItemId))))
            .or(() -> Optional.ofNullable(presMinMap.get(key.setRmhCategoryId(emptyDictionaryItemId))))
            .or(() -> Optional.ofNullable(presMinMap.get(key.setRmhGenderAgeId(emptyDictionaryItemId))))
            .or(() -> Optional.ofNullable(presMinMap.get(key.setBrandId(emptyDictionaryItemId))))
            .or(() -> Optional.ofNullable(presMinMap.get(key.setRmhProductDivisionId(emptyDictionaryItemId))))
            .orElse(MIN_PRES_MIN);
    }
}
