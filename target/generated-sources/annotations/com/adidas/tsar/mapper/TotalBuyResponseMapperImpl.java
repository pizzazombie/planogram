package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.TotalBuy;
import com.adidas.tsar.dto.ArticleDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-09-01T16:33:20+0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.16.1 (Amazon.com Inc.)"
)
@Component
public class TotalBuyResponseMapperImpl implements TotalBuyResponseMapper {

    @Override
    public TotalBuyResponse toResponse(TotalBuy source, ArticleDto article) {
        if ( source == null && article == null ) {
            return null;
        }

        long id = 0L;
        int sizeIndex = 0;
        int quantity = 0;
        if ( source != null ) {
            id = source.getId();
            if ( source.getSizeIndex() != null ) {
                sizeIndex = Integer.parseInt( source.getSizeIndex() );
            }
            quantity = source.getQuantity();
        }
        String articleName = null;
        String articleCode = null;
        if ( article != null ) {
            articleName = article.getName();
            articleCode = article.getCode();
        }

        TotalBuyResponse totalBuyResponse = new TotalBuyResponse( id, articleName, articleCode, sizeIndex, quantity );

        if ( source != null ) {
            totalBuyResponse.setModified( source.getModifiedDate() );
            totalBuyResponse.setModifiedBy( source.getModifiedBy() );
        }

        return totalBuyResponse;
    }
}
