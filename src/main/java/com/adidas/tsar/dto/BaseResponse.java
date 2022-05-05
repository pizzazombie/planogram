package com.adidas.tsar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ApiModel(description = "Success response")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BaseResponse<T> {

    @ApiModelProperty(value = "Common request information", required = true)
    @JsonProperty("info")
    private Info info;

    @ApiModelProperty(value = "Parameters of response", required = true)
    @JsonProperty("params")
    private Params params;

    @ApiModelProperty(value = "Payload")
    @JsonProperty("data")
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Info {

        @ApiModelProperty(value = "Section title", required = true, example = "Import the FTW Priority List")
        @JsonProperty("title")
        private String title;

        @ApiModelProperty(value = "Detail information")
        @JsonProperty("details")
        private String details;

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Params {

        @ApiModelProperty(value = "Page information", required = true)
        @JsonProperty("page")
        public Page page;

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Page {

        @ApiModelProperty(value = "Count of total pages", example = "1200")
        @JsonProperty("total")
        private Integer total;

        @ApiModelProperty(value = "Number of current page", example = "4")
        @JsonProperty("current")
        private Integer current;

        @ApiModelProperty(value = "Page size", example = "50")
        @JsonProperty("size")
        private Integer size;

        @ApiModelProperty(value = "Count of total rows", example = "50000")
        @JsonProperty("totalRows")
        private long totalRows;

    }

}
