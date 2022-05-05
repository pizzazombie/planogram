package com.adidas.tsar.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class RmhProductDivisionDto implements DictionaryEntity {

    private Integer id;

    private String name;

}
