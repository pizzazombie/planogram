package com.adidas.tsar.dto.ridred;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RidredCreateDto {

    @NotEmpty(message = "Article cannot be empty")
    @JsonProperty("article")
    private String article;

    @JsonProperty("rid")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rid;

    @JsonProperty("red")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate red;

}
