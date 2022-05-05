package com.adidas.tsar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Collection;

@ApiModel(description = "Error response")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BaseErrorResponse {

    @ApiModelProperty(value = "Common info about error", required = true)
    @JsonProperty("info")
    private Info info;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Info {
        @ApiModelProperty(value = "Section title", required = true, example = "Import the FTW Priority List")
        @JsonProperty("title")
        private String title;
        @ApiModelProperty(value = "Error information", required = true)
        @JsonProperty("error")
        private Error error;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class Error {

        @ApiModelProperty(value = "Error title", required = true, example = "Import failed")
        @JsonProperty("title")
        private String title;

        @ApiModelProperty(value = "Http code", required = true, example = "400")
        @JsonProperty("status")
        private int status;

        @ApiModelProperty(value = "Error description", example = "Validation was failed")
        @JsonProperty("detail")
        private String detail;

        @ApiModelProperty(value = "List of inner exceptions")
        @JsonProperty("innerErrors")
        private Collection<InnerError> innerErrors;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class InnerError {

        @ApiModelProperty(value = "Title of inner exception", example = "Incorrect BRAND field format: addidas. Row 1, addidas|MEN|1")
        @JsonProperty("title")
        private String title;

        @ApiModelProperty(value = "Description")
        @JsonProperty("detail")
        private String detail;
    }

}