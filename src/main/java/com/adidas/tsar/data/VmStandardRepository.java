package com.adidas.tsar.data;

import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface VmStandardRepository extends JpaRepository<VmStandard, Long> {

    List<VmStandard> findAllByBrandIdInAndRmhGenderAgeIdInAndRmhCategoryIdInAndRmhProductTypeIdInAndRmhProductDivisionIdInAndSizeScaleIdIn(
        Collection<Integer> brandIds,
        Collection<Integer> rmhGenderAgeIds,
        Collection<Integer> rmhCategoryIds,
        Collection<Integer> rmhProductTypeIds,
        Collection<Integer> rmhProductDivisionIds,
        Collection<Integer> sizeScaleIds
    );

    default List<VmStandard> findAllByKeys(Collection<VmStandardKey> keys) {
        Set<Integer> brandIds = new HashSet<>();
        Set<Integer> rmhGenderAgeIds = new HashSet<>();
        Set<Integer> rmhCategoryIds = new HashSet<>();
        Set<Integer> rmhProductTypeIds = new HashSet<>();
        Set<Integer> rmhProductDivisionIds = new HashSet<>();
        Set<Integer> sizeScaleIds = new HashSet<>();

        keys.forEach(key -> {
            brandIds.add(key.getBrandId());
            rmhGenderAgeIds.add(key.getRmhGenderAgeId());
            rmhCategoryIds.add(key.getRmhCategoryId());
            rmhProductTypeIds.add(key.getRmhProductTypeId());
            rmhProductDivisionIds.add(key.getRmhProductDivisionId());
            sizeScaleIds.add(key.getSizeScaleId());
        });

        return findAllByBrandIdInAndRmhGenderAgeIdInAndRmhCategoryIdInAndRmhProductTypeIdInAndRmhProductDivisionIdInAndSizeScaleIdIn(
            brandIds,
            rmhGenderAgeIds,
            rmhCategoryIds,
            rmhProductTypeIds,
            rmhProductDivisionIds,
            sizeScaleIds
        );
    }

}