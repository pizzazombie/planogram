package com.adidas.tsar.data;

import com.adidas.tsar.domain.Removal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RemovalRepository extends JpaRepository<Removal, Long> {

    @Modifying
    @Query(value = "truncate table removals", nativeQuery = true)
    void truncate();
}
