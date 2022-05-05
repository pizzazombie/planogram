package com.adidas.tsar.dto.totalbuy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TotalBuyCreateDto {

    @NotEmpty(message = "Article cannot be empty")
    @JsonProperty("article")
    private String article;

    @NotEmpty(message = "SizeIndex cannot be empty")
    @Pattern(regexp = "^[0-9]{3}$", message = "sizeIndex should have only 3 numbers")
    @JsonProperty("sizeIndex")
    private String sizeIndex;

    @Min(value = 0, message = "Quantity should be above then 0")
    @NotNull(message = "Quantity cannot be null")
    @JsonProperty("quantity")
    private Integer quantity;

}
