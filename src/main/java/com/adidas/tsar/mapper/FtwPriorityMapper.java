package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityExcelDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FtwPriorityMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "rmhGenderAge", source = "rmhGenderAge.name")
    FtwPriorityResponse toResponse(FtwPriority source, BrandDto brand, RmhGenderAgeDto rmhGenderAge);

    @Mapping(target = "rowNumber", ignore = true)
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "rmhGenderAge", source = "rmhGenderAge.name")
    FtwPriorityExcelDto toExportDto(FtwPriority source, BrandDto brand, RmhGenderAgeDto rmhGenderAge);

    default FtwPriority getFtwPriority(BrandDto brand, RmhGenderAgeDto rmhGenderAge, String sizeIndex, Integer priority, String user) {
        FtwPriority ftwPriority = new FtwPriority()
            .setBrandId(brand.getId())
            .setRmhGenderAgeId(rmhGenderAge.getId())
            .setSizeIndex(sizeIndex);
        ftwPriority.changePriority(priority, user);
        return ftwPriority;
    }

}
