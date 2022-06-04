package com.victor.library.service;

import com.victor.library.model.entity.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
