package com.adidas.tsar.dto.ftwpriority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FtwPriorityCreateDto {

    @NotNull(message = "Brand cannot be null")
    @JsonProperty("brand")
    private String brand;

    @NotNull(message = "RmhGenderAge cannot be null")
    @JsonProperty("rmhGenderAge")
    private String rmhGenderAge;

    @NotNull(message = "SizeIndex cannot be null")
    @Pattern(regexp = "^[0-9]{3}$", message = "sizeIndex should have only 3 numbers")
    @JsonProperty("sizeIndex")
    private String sizeIndex;

    @NotNull(message = "Priority cannot be null")
    @JsonProperty("priority")
    private Integer priority;

}
