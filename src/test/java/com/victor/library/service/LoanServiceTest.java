package com.victor.library.service;

import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import com.victor.library.model.repository.LoanRepository;
import com.victor.library.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a new loan")
    public void saveLoanTest(){
        Book book = Book.builder().id(1l).build();
        Loan saving = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1l)
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(saving)).thenReturn(savedLoan);

        Loan loan = service.save(saving);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Should throw a business exception if book has already loaned")
    public void loanedBookSaveTest(){
        Book book = Book.builder().id(1l).build();
        Loan saving = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(()-> service.save(saving));

        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Book already loaned");

        verify(repository, never()).save(saving);
    }
}
