package com.victor.library.model.repository;

import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should verify is exists loan not returned to a book")
    public void existsByBookAndNotReturnedTest(){
        // cenário
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        entityManager.persist(loan);

        // execução
        boolean exists = repository.existsByBookAndNotReturned(book);

        // verificações
        assertThat(exists).isTrue();
    }

    private Book createNewBook() {
        return Book.builder()
                .title("Contos")
                .author("Victor")
                .isbn("1234")
                .build();
    }
}
