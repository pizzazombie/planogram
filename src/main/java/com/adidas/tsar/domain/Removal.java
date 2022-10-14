package com.adidas.tsar.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "removals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Removal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "articleId", nullable = false)
    private long articleId;

    @Column(name = "storeId", nullable = false)
    private int storeId;

//    @Column(name = "sap", nullable = false)
//    private String sap;

    @Column(name = "removalNumber", nullable = false)
    private String removalNumber;

}
