package com.adidas.tsar.dto.ftwpriority;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@ApiModel(description = "FTW Priority response")
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FtwPriorityResponse {

    @JsonProperty("id")
    private final long id;

    @JsonProperty("brand")
    private final String brand;

    @JsonProperty("rmh_gender_age")
    private final String rmhGenderAge;

    @JsonProperty("size_index")
    private final int sizeIndex;

    @JsonProperty("priority")
    private final int priority;

    @JsonProperty("modified_date")
    private LocalDateTime modifiedDate;

    @JsonProperty("modified_by")
    private String modifiedBy;

}
