package com.victor.library.api.resource;

import com.victor.library.api.dto.LoanDTO;
import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import com.victor.library.service.BookService;
import com.victor.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Long create(@RequestBody LoanDTO dto){
         Book book = bookService.getBookByIsbn(dto.getIsbn()).get();
         Loan entity = Loan.builder()
                 .book(book)
                 .customer(dto.getCustomer())
                 .loanDate(LocalDate.now())
                 .build();

         entity = loanService.save(entity);
         return entity.getId();
    }
}
