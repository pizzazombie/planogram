package com.adidas.tsar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BaseRequest<T> {

    @Valid
    @NotNull
    @JsonProperty("data")
    private T data;

}
