package com.adidas.tsar.data;

import com.adidas.tsar.domain.Planogram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanogramRepository extends JpaRepository<Planogram, Long>, JpaSpecificationExecutor<Planogram> {

    List<Planogram> findAllByStoreCode(String storeCode);

}
