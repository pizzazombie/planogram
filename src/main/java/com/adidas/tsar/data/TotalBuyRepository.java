package com.adidas.tsar.data;

import com.adidas.tsar.domain.TotalBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TotalBuyRepository extends JpaRepository<TotalBuy, Long> {

    @Modifying
    @Query(value = "truncate table totalBuy", nativeQuery = true)
    void truncate();

    List<TotalBuy> findTotalBuyByArticleIdOrderByQuantityDesc(int articleId);

}