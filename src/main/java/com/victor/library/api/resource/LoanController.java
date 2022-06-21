package com.victor.library.api.resource;

import com.victor.library.api.dto.BookDTO;
import com.victor.library.api.dto.LoanDTO;
import com.victor.library.api.dto.LoanFilterDTO;
import com.victor.library.api.dto.ReturnedLoanDTO;
import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import com.victor.library.service.BookService;
import com.victor.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public Long create(@RequestBody LoanDTO dto){
         Book book = bookService.getBookByIsbn(dto.getIsbn())
                 .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Book not found for passed isbn"));

         Loan entity = Loan.builder()
                 .book(book)
                 .customer(dto.getCustomer())
                 .loanDate(LocalDate.now())
                 .build();

         entity = loanService.save(entity);
         return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> loans = result
            .getContent()
            .stream()
            .map( entity ->  {
                Book book = entity.getBook();
                BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                loanDTO.setBook(bookDTO);

                return loanDTO;
            }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}
