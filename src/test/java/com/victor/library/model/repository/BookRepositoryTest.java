package com.victor.library.model.repository;

import com.victor.library.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Should return true when exists a book with the ISBN provided")
    public void returnTrueWhenIsbnExistsTest() {

        // cenário
        String isbn = "1234";
        Book book = createNewBook();

        entityManager.persist(book);

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when doesnt exists a book with the ISBN provided")
    public void returnFalseWhenIsbnDoesntExistsTest() {

        // cenário
        String isbn = "1234";

        // execução
        boolean exists = repository.existsByIsbn(isbn);

        // verificação
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return a book by id")
    public void findByIdBookTest() {
        // cenário
        Book book = createNewBook();
        entityManager.persist(book);

        // execução
        Optional<Book> foundBook = repository.findById(book.getId());

        // verificações
        assertThat(foundBook.isPresent()).isTrue();
    }

    private Book createNewBook() {
        return Book.builder()
                .title("Contos")
                .author("Victor")
                .isbn("1234")
                .build();
    }
}
