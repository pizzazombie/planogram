package com.adidas.tsar.dto.ftwpriority;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FtwPriorityKeyDto implements FtwPriorityKey {

    private final int brandId;
    private final int rmhGenderAgeId;
    private final String sizeIndex;

}
