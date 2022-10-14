package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.Planogram;
import com.adidas.tsar.dto.ArticleDto.SkuResponseDto;
import com.adidas.tsar.dto.planogram.PlanogramResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-01T16:33:20+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16.1 (Amazon.com Inc.)"
)
@Component
public class PlanogramMapperImpl implements PlanogramMapper {

    @Override
    public PlanogramResponseDto toResponse(Planogram planogram, SkuResponseDto sku) {
        if ( planogram == null && sku == null ) {
            return null;
        }

        PlanogramResponseDto planogramResponseDto = new PlanogramResponseDto();

        if ( planogram != null ) {
            planogramResponseDto.setSizeIndex( planogram.getSizeIndex() );
            planogramResponseDto.setModified( planogram.getCalculatedAt() );
            planogramResponseDto.setArticleCode( planogram.getArticleCode() );
            planogramResponseDto.setStoreCode( planogram.getStoreCode() );
            planogramResponseDto.setPriority( planogram.getPriority() );
            planogramResponseDto.setFinalSalesFloorQty( planogram.getFinalSalesFloorQty() );
            planogramResponseDto.setIgnoreForReverseReplenishment( planogram.isIgnoreForReverseReplenishment() );
        }
        if ( sku != null ) {
            planogramResponseDto.setGtin( sku.getGtin() );
            planogramResponseDto.setLocalSize( sku.getLocalSize() );
        }

        return planogramResponseDto;
    }
}
