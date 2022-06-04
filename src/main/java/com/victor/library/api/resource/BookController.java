package com.victor.library.api.resource;

import com.victor.library.api.dto.BookDTO;
import com.victor.library.api.exception.ApiErrors;
import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

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
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(OK)
    public BookDTO get(@PathVariable Long id){
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, BookDTO dto){
        return service.getById(id)
            .map( book -> {
                book.setAuthor(dto.getAuthor());
                book.setTitle(dto.getTitle());
                book = service.update(book);
                return modelMapper.map(book, BookDTO.class);
            }).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex){
        return new ApiErrors(ex);
    }
}
