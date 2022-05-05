package com.adidas.tsar.dto;

import lombok.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ArticleDto implements DictionaryEntity {

    private Integer id;
    private String name;
    private String code;
    @Nullable
    private String brand;
    @Nullable
    private String genderAge;
    @Nullable
    private String category;
    @Nullable
    private String productType;
    @Nullable
    private String productDivision;
    @Nullable
    private String sizeScale;
    private LocalDateTime modifiedDate;
    private List<SkuResponseDto> skus = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode(of = "id", callSuper = false)
    public static class SkuResponseDto {
        private Integer id;
        private String gtin;
        private String localSize;
        private String sizeIndex;
    }

}
