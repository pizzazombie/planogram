package com.adidas.tsar.dto.vmstandard;

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
public class VmStandardResponse {

    @JsonProperty("id")
    private final long id;

    @JsonProperty("brand")
    private final String brand;

    @JsonProperty("rmhGenderAge")
    private final String rmhGenderAge;

    @JsonProperty("rmhCategory")
    private final String rmhCategory;

    @JsonProperty("rmhProductType")
    private final String rmhProductType;

    @JsonProperty("rmhProductDivision")
    private final String rmhProductDivision;

    @JsonProperty("sizeScale")
    private final String sizeScale;

    @JsonProperty("presMin")
    private final int presMin;

    @JsonProperty("modified")
    private LocalDateTime modifiedDate;

    @JsonProperty("modifiedBy")
    private String modifiedBy;

}