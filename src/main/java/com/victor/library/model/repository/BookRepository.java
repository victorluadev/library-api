package com.victor.library.model.repository;

import com.victor.library.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
