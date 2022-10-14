package com.adidas.tsar.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "matrix")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class Matrix implements Serializable {

    @Id
    private long id;

    @Column(name = "articleId", nullable = false)
    private long articleId;

    @Column(name = "SizeIndex", nullable = false)
    private String sizeIndex;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "storeId", nullable = false)
    private int storeId;

    public Matrix(long articleId, String sizeIndex, int quantity, int storeId) {
        this.articleId = articleId;
        this.sizeIndex = sizeIndex;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return articleId == matrix.articleId && quantity == matrix.quantity && storeId == matrix.storeId && Objects.equals(sizeIndex, matrix.sizeIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, sizeIndex, quantity, storeId);
    }

}
