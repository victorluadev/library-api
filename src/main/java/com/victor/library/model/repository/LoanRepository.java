package com.victor.library.model.repository;

import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookAndNotReturned(Book book);
}
