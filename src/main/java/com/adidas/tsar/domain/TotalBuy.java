package com.adidas.tsar.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "totalBuy")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class TotalBuy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "articleId", nullable = false)
    private long articleId;

    @Column(name = "sizeIndex", nullable = false)
    private String sizeIndex;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "modifiedBy", nullable = false)
    private String modifiedBy;

    @Column(name = "modifiedDate", nullable = false)
    private LocalDateTime modifiedDate;

}
