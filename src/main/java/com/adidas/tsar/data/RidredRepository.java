package com.adidas.tsar.data;


import com.adidas.tsar.domain.Ridred;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RidredRepository extends JpaRepository<Ridred, Long>  {

    @Modifying
    @Query(value = "truncate table ridred", nativeQuery = true)
    void truncate();
}
