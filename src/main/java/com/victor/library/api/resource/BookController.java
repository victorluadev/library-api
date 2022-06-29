package com.victor.library.api.resource;

import com.victor.library.api.dto.BookDTO;
import com.victor.library.api.dto.LoanDTO;
import com.victor.library.api.exception.ApiErrors;
import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import com.victor.library.service.BookService;
import com.victor.library.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Create a book")
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(OK)
    @ApiOperation("Get a book by id")
    public BookDTO get(@PathVariable Long id){
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Delete a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted")
    })
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book by id")
    public BookDTO update(@PathVariable Long id, BookDTO dto){
        return service.getById(id)
            .map( book -> {
                book.setAuthor(dto.getAuthor());
                book.setTitle(dto.getTitle());
                book = service.update(book);
                return modelMapper.map(book, BookDTO.class);
            }).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDTO> find(BookDTO dto, Pageable pageable) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageable);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageable, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = service.getById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND)
        );

        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> list = result.getContent().stream().map(
                loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);

                    return loanDTO;
                }
        ).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable,result.getTotalElements());

    }
}
