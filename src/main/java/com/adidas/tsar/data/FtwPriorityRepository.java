package com.adidas.tsar.data;

import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface FtwPriorityRepository extends JpaRepository<FtwPriority, Long> {

    List<FtwPriority> findAllByBrandIdInAndRmhGenderAgeIdInAndSizeIndexIn(Collection<Integer> brands, Collection<Integer> ages, Collection<String> sizeIndexes);

    default List<FtwPriority> findAllByKeys(Collection<FtwPriorityKey> keys) {
        Set<Integer> brandIds = new HashSet<>();
        Set<Integer> rmhGenderAgeIds = new HashSet<>();
        Set<String> sizeIndexes = new HashSet<>();

        keys.forEach(ftwPriorityKey -> {
            brandIds.add(ftwPriorityKey.getBrandId());
            rmhGenderAgeIds.add(ftwPriorityKey.getRmhGenderAgeId());
            sizeIndexes.add(ftwPriorityKey.getSizeIndex());
        });

        return findAllByBrandIdInAndRmhGenderAgeIdInAndSizeIndexIn(brandIds, rmhGenderAgeIds, sizeIndexes);
    }

}
