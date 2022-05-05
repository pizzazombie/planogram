package com.adidas.tsar.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "matrix")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Matrix implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "articleId", nullable = false)
    private long articleId;

    @Column(name = "SizeIndex", nullable = false)
    private String sizeIndex;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "sap", nullable = false)
    private String sap;

}
