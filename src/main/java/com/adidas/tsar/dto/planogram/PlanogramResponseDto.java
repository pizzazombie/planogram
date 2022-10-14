package com.adidas.tsar.dto.planogram;

import com.adidas.tsar.common.SearchUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonFilter(SearchUtils.SEARCH_FILTER_NAME)
public class PlanogramResponseDto {

    @JsonProperty("articleCode")
    private String articleCode;

    @JsonProperty("storeCode")
    private String storeCode;

    @JsonProperty("sizeIndex")
    private String sizeIndex;

    @JsonProperty("gtin")
    private String gtin;

    @JsonProperty("localSize")
    private String localSize;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("finalSalesFloorQty")
    private int finalSalesFloorQty;

    @JsonProperty("ignoreForReverseReplenishment")
    private boolean ignoreForReverseReplenishment;

    @JsonProperty("modified")
    private LocalDateTime modified;

}
