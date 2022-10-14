package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityExcelDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-01T16:33:20+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16.1 (Amazon.com Inc.)"
)
@Component
public class FtwPriorityMapperImpl implements FtwPriorityMapper {

    @Override
    public FtwPriorityResponse toResponse(FtwPriority source, BrandDto brand, RmhGenderAgeDto rmhGenderAge) {
        if ( source == null && brand == null && rmhGenderAge == null ) {
            return null;
        }

        long id = 0L;
        int sizeIndex = 0;
        int priority = 0;
        if ( source != null ) {
            id = source.getId();
            if ( source.getSizeIndex() != null ) {
                sizeIndex = Integer.parseInt( source.getSizeIndex() );
            }
            priority = source.getPriority();
        }
        String brand1 = null;
        if ( brand != null ) {
            brand1 = brand.getName();
        }
        String rmhGenderAge1 = null;
        if ( rmhGenderAge != null ) {
            rmhGenderAge1 = rmhGenderAge.getName();
        }

        FtwPriorityResponse ftwPriorityResponse = new FtwPriorityResponse( id, brand1, rmhGenderAge1, sizeIndex, priority );

        if ( source != null ) {
            ftwPriorityResponse.setModifiedDate( source.getModifiedDate() );
            ftwPriorityResponse.setModifiedBy( source.getModifiedBy() );
        }

        return ftwPriorityResponse;
    }

    @Override
    public FtwPriorityExcelDto toExportDto(FtwPriority source, BrandDto brand, RmhGenderAgeDto rmhGenderAge) {
        if ( source == null && brand == null && rmhGenderAge == null ) {
            return null;
        }

        FtwPriorityExcelDto ftwPriorityExcelDto = new FtwPriorityExcelDto();

        if ( source != null ) {
            ftwPriorityExcelDto.setSizeIndex( source.getSizeIndex() );
            ftwPriorityExcelDto.setPriority( String.valueOf( source.getPriority() ) );
        }
        if ( brand != null ) {
            ftwPriorityExcelDto.setBrand( brand.getName() );
        }
        if ( rmhGenderAge != null ) {
            ftwPriorityExcelDto.setRmhGenderAge( rmhGenderAge.getName() );
        }

        return ftwPriorityExcelDto;
    }
}
