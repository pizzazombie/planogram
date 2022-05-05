package com.adidas.tsar.dto.vmstandard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VmStandardEditDto {

    @NotNull(message = "PresMin cannot be null")
    @JsonProperty("presMin")
    private Integer presMin;

}
