package com.adidas.tsar.mapper;

import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FtwPriorityFactory {

    public FtwPriority getFtwPriority(BrandDto brand, RmhGenderAgeDto rmhGenderAge, String sizeIndex, Integer priority, String user) {
        FtwPriority ftwPriority = new FtwPriority()
            .setBrandId(brand.getId())
            .setRmhGenderAgeId(rmhGenderAge.getId())
            .setSizeIndex(sizeIndex);
        ftwPriority.changePriority(priority, user);
        return ftwPriority;

    }

}
