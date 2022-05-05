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
public class FtwPriorityEditDto {

    @NotNull(message = "Priority cannot be null")
    @JsonProperty("priority")
    private Integer priority;

}
