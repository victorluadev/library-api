package com.victor.library.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(length = 100)
    private String customer;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;

    @Column
    private LocalDate loanDate;

    @Column
    private Boolean returned;
}
