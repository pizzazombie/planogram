package com.adidas.tsar.domain;

import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.dto.vmstandard.VmStandardKeyImpl;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "vmStandards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class VmStandard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "brandId", nullable = false)
    private int brandId;

    @Column(name = "rmhGenderAgeId")
    private int rmhGenderAgeId;

    @Column(name = "rmhCategoryId")
    private int rmhCategoryId;

    @Column(name = "rmhProductTypeId", nullable = false)
    private int rmhProductTypeId;

    @Column(name = "rmhProductDivisionId")
    private int rmhProductDivisionId;

    @Column(name = "sizeScaleId")
    private int sizeScaleId;

    @Column(name = "presMin")
    private int presMin;

    @Column(name = "modifiedBy", nullable = false)
    private String modifiedBy;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    public void changePresMin(@Nullable Integer presMin, String user) {
        this.presMin = presMin;
        this.modifiedBy = user;
        this.modifiedDate = LocalDateTime.now();
    }

    public VmStandardKey buildKey() {
        return new VmStandardKeyImpl(this);
    }

}
