package com.adidas.tsar.data;

import com.adidas.tsar.domain.Matrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatrixRepository extends JpaRepository<Matrix, Long> {

    @Modifying
    @Query(value = "truncate table matrix", nativeQuery = true)
    void truncate();

    List<Matrix> findByArticleIdAndSizeIndexIn(Long articleId, List<String> sizeIndexes);

}
