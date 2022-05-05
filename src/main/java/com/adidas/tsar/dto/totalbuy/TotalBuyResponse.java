package com.adidas.tsar.dto.totalbuy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TotalBuyResponse {

    @JsonProperty("id")
    private final long id;

    @JsonProperty("articleName")
    private final String articleName;

    @JsonProperty("articleCode")
    private final String articleCode;

    @JsonProperty("sizeIndex")
    private final int sizeIndex;

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("modified")
    private LocalDateTime modified;

    @JsonProperty("modifiedBy")
    private String modifiedBy;

}
