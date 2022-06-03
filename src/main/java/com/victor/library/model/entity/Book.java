package com.victor.library.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_book")
public class Book {

    @Id
    @Column
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "nm_title")
    private String title;

    @Column(name = "nm_author")
    private String author;

    @Column(name = "ds_isbn")
    private String isbn;
}
