package com.adidas.tsar.domain;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ridred")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Ridred implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "articleId", nullable = false)
    private long articleId;

    @Column(name = "rid", nullable = false)
    public LocalDate rid;

    @Column(name = "red", nullable = false)
    public LocalDate red;


}
