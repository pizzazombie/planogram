package com.adidas.tsar.domain;

import com.adidas.tsar.dto.ftwpriority.FtwPriorityKey;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKeyDto;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ftwPriority")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class FtwPriority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "brandId", nullable = false)
    private int brandId;

    @Column(name = "rmhGenderAgeId", nullable = false)
    private int rmhGenderAgeId;

    @Column(name = "sizeIndex", nullable = false)
    private String sizeIndex;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "modifiedBy", nullable = false)
    private String modifiedBy;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

    public void changePriority(Integer priority, String currentUser) {
        this.priority = priority;
        this.modifiedBy = currentUser;
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean isEqualByKey(FtwPriority another) {
        return this.brandId == another.getBrandId() &&
            this.getRmhGenderAgeId() == another.getRmhGenderAgeId() &&
            this.getSizeIndex().equals(another.getSizeIndex());
    }

    public FtwPriorityKey buildKey() {
        return new FtwPriorityKeyDto(brandId, rmhGenderAgeId, sizeIndex);
    }
}
