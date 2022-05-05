package com.adidas.tsar.dto.matrix;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MatrixCreateDto {

    @NotEmpty(message = "Article cannot be empty")
    @JsonProperty("article")
    private String article;

    @NotEmpty(message = "Sap cannot be empty")
    @JsonProperty("sap")
    private String sap;

    @NotEmpty(message = "SizeIndex cannot be empty")
    @Pattern(regexp = "^[0-9]{3}$", message = "sizeIndex should have only 3 numbers")
    @JsonProperty("sizeIndex")
    private String sizeIndex;

    @Min(value = 0, message = "Quantity should be above then 0")
    @NotNull(message = "Quantity cannot be null")
    @JsonProperty("quantity")
    private Integer quantity;

}
