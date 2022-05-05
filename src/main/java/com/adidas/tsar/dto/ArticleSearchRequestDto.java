package com.adidas.tsar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ArticleSearchRequestDto {

    @JsonProperty("ids")
    private Collection<Long> ids;

    @JsonProperty("codes")
    private Collection<String> codes;

}
