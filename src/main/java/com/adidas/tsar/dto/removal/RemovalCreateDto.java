package com.adidas.tsar.dto.removal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RemovalCreateDto {

    @NotEmpty(message = "Article cannot be empty")
    @JsonProperty("article")
    private String article;

    @NotEmpty(message = "Sap cannot be empty")
    @JsonProperty("sap")
    private String sap;

    @NotEmpty(message = "Removal number cannot be empty")
    @JsonProperty("removalNum")
    private String removalNum;
}
