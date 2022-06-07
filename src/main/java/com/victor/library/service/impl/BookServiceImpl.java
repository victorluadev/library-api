package com.victor.library.service.impl;

import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.model.repository.BookRepository;
import com.victor.library.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Cannot save duplicated Isbn");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book.getId() == null || book == null) {
            throw new IllegalArgumentException("Book id cannot be null");
        }
        this.repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book.getId() == null || book == null) {
            throw new IllegalArgumentException("Book id cannot be null");
        }
        return this.repository.save(book);
    }
}
