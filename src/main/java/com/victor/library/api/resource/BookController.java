package com.victor.library.api.resource;

import com.victor.library.api.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(CREATED)
    public BookDTO create() {
        BookDTO dto = BookDTO.builder()
                .id(1L)
                .author("Victor")
                .title("Meu livro")
                .isbn("123321")
                .build();

        return dto;
    }
}
