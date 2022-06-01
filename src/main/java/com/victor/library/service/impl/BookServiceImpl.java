package com.victor.library.service.impl;

import com.victor.library.model.entity.Book;
import com.victor.library.model.repository.BookRepository;
import com.victor.library.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
