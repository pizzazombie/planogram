package com.adidas.tsar.dto.planogram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PlanogramSearchRequestDto {

    @JsonProperty("fields")
    private List<String> fields;

}
