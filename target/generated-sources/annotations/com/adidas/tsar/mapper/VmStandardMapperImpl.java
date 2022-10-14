package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhCategoryDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.RmhProductDivisionDto;
import com.adidas.tsar.dto.RmhProductTypeDto;
import com.adidas.tsar.dto.SizeScaleDto;
import com.adidas.tsar.dto.vmstandard.VmStandardExcelDto;
import com.adidas.tsar.dto.vmstandard.VmStandardResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-01T16:33:19+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16.1 (Amazon.com Inc.)"
)
@Component
public class VmStandardMapperImpl implements VmStandardMapper {

    @Override
    public VmStandardResponse toResponse(VmStandard source, BrandDto brand, RmhGenderAgeDto rmhGenderAge, RmhCategoryDto rmhCategory, RmhProductTypeDto rmhProductType, RmhProductDivisionDto rmhProductDivision, SizeScaleDto sizeScale) {
        if ( source == null && brand == null && rmhGenderAge == null && rmhCategory == null && rmhProductType == null && rmhProductDivision == null && sizeScale == null ) {
            return null;
        }

        long id = 0L;
        int presMin = 0;
        if ( source != null ) {
            id = source.getId();
            presMin = source.getPresMin();
        }
        String brand1 = null;
        if ( brand != null ) {
            brand1 = brand.getName();
        }
        String rmhGenderAge1 = null;
        if ( rmhGenderAge != null ) {
            rmhGenderAge1 = rmhGenderAge.getName();
        }
        String rmhCategory1 = null;
        if ( rmhCategory != null ) {
            rmhCategory1 = rmhCategory.getName();
        }
        String rmhProductType1 = null;
        if ( rmhProductType != null ) {
            rmhProductType1 = rmhProductType.getName();
        }
        String rmhProductDivision1 = null;
        if ( rmhProductDivision != null ) {
            rmhProductDivision1 = rmhProductDivision.getName();
        }
        String sizeScale1 = null;
        if ( sizeScale != null ) {
            sizeScale1 = sizeScale.getName();
        }

        VmStandardResponse vmStandardResponse = new VmStandardResponse( id, brand1, rmhGenderAge1, rmhCategory1, rmhProductType1, rmhProductDivision1, sizeScale1, presMin );

        if ( source != null ) {
            vmStandardResponse.setModifiedDate( source.getModifiedDate() );
            vmStandardResponse.setModifiedBy( source.getModifiedBy() );
        }

        return vmStandardResponse;
    }

    @Override
    public VmStandardExcelDto toExcelDto(VmStandard source, BrandDto brand, RmhGenderAgeDto rmhGenderAge, RmhCategoryDto rmhCategory, RmhProductTypeDto rmhProductType, RmhProductDivisionDto rmhProductDivision, SizeScaleDto sizeScale) {
        if ( source == null && brand == null && rmhGenderAge == null && rmhCategory == null && rmhProductType == null && rmhProductDivision == null && sizeScale == null ) {
            return null;
        }

        VmStandardExcelDto vmStandardExcelDto = new VmStandardExcelDto();

        if ( source != null ) {
            vmStandardExcelDto.setPresMin( String.valueOf( source.getPresMin() ) );
        }
        if ( brand != null ) {
            vmStandardExcelDto.setBrand( brand.getName() );
        }
        if ( rmhGenderAge != null ) {
            vmStandardExcelDto.setRmhGenderAge( rmhGenderAge.getName() );
        }
        if ( rmhCategory != null ) {
            vmStandardExcelDto.setRmhCategory( rmhCategory.getName() );
        }
        if ( rmhProductType != null ) {
            vmStandardExcelDto.setRmhProductType( rmhProductType.getName() );
        }
        if ( rmhProductDivision != null ) {
            vmStandardExcelDto.setRmhProductDivision( rmhProductDivision.getName() );
        }
        if ( sizeScale != null ) {
            vmStandardExcelDto.setSizeScale( sizeScale.getName() );
        }

        return vmStandardExcelDto;
    }
}
