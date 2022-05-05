package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.*;
import com.adidas.tsar.dto.*;
import com.adidas.tsar.dto.vmstandard.VmStandardExcelDto;
import com.adidas.tsar.dto.vmstandard.VmStandardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VmStandardMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "rmhGenderAge", source = "rmhGenderAge.name")
    @Mapping(target = "rmhCategory", source = "rmhCategory.name")
    @Mapping(target = "rmhProductType", source = "rmhProductType.name")
    @Mapping(target = "rmhProductDivision", source = "rmhProductDivision.name")
    @Mapping(target = "sizeScale", source = "sizeScale.name")
    VmStandardResponse toResponse(VmStandard source, BrandDto brand, RmhGenderAgeDto rmhGenderAge, RmhCategoryDto rmhCategory, RmhProductTypeDto rmhProductType, RmhProductDivisionDto rmhProductDivision, SizeScaleDto sizeScale);

    @Mapping(target = "rowNumber", ignore = true)
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "rmhGenderAge", source = "rmhGenderAge.name")
    @Mapping(target = "rmhCategory", source = "rmhCategory.name")
    @Mapping(target = "rmhProductType", source = "rmhProductType.name")
    @Mapping(target = "rmhProductDivision", source = "rmhProductDivision.name")
    @Mapping(target = "sizeScale", source = "sizeScale.name")
    VmStandardExcelDto toExcelDto(VmStandard source, BrandDto brand, RmhGenderAgeDto rmhGenderAge, RmhCategoryDto rmhCategory, RmhProductTypeDto rmhProductType, RmhProductDivisionDto rmhProductDivision, SizeScaleDto sizeScale);
}
