package com.adidas.tsar.dto.vmstandard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VmStandardCreateDto {

    @NotNull(message = "Brand cannot be null")
    @JsonProperty("brand")
    private String brand;

    @JsonProperty("rmhGenderAge")
    private String rmhGenderAge;

    @JsonProperty("rmhCategory")
    private String rmhCategory;

    @NotNull(message = "RmhProductType cannot be null")
    @JsonProperty("rmhProductType")
    private String rmhProductType;

    @JsonProperty("rmhProductDivision")
    private String rmhProductDivision;

    @NotNull(message = "SizeIndex cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{2}$", message = "sizeScale should have 2 letters")
    @JsonProperty("sizeScale")
    private String sizeScale;

    @NotNull(message = "PresMin cannot be null")
    @JsonProperty("presMin")
    private Integer presMin;

}
