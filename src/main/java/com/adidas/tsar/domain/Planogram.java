package com.adidas.tsar.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Planogram {

    @Column(name = "articleCode", nullable = false)
    private String articleCode;

    @Column(name = "storeCode", nullable = false)
    private String storeCode;

    @Column(name = "sizeIndex", nullable = false)
    private String sizeIndex;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "presMin", nullable = false)
    private int presMin;

    @Column(name = "salesFloorQty", nullable = false)
    private int salesFloorQty;

    @Column(name = "finalSalesFloorQty", nullable = false)
    private int finalSalesFloorQty;

    @Column(name = "ignoreForReverseReplenishment", nullable = false)
    private int ignoreForReverseReplenishment;

}
