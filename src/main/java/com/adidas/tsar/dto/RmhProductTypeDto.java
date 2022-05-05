package com.adidas.tsar.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class RmhProductTypeDto implements DictionaryEntity {

    private Integer id;

    private String name;

}
