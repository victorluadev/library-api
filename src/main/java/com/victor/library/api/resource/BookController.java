package com.victor.library.api.resource;

import com.victor.library.api.dto.BookDTO;
import com.victor.library.model.entity.Book;
import com.victor.library.service.BookService;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }
}
