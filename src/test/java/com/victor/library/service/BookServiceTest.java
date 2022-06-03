package com.victor.library.service;

import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.model.repository.BookRepository;
import com.victor.library.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl( repository );
    }

    @Test
    @DisplayName("Should save a new book")
    public void saveBookTest() {

        // cenário
        Book book = Book.builder()
                .isbn("1234")
                .author("Maria")
                .title("Aventuras de Maria")
                .build();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(createValidBook());

        // execução
        Book savedBook = service.save(book);

        // verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getAuthor()).isEqualTo("Maria");
        assertThat(savedBook.getTitle()).isEqualTo("Aventuras de Maria");
    }

    @Test
    @DisplayName("Should throw business error if try to save duplicated Isbn")
    public void shouldNotSaveABookWithDuplicatedISBNTest(){

        // cenário
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execução
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        // verificações
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot save duplicated Isbn");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book createValidBook() {
        return Book.builder()
                .id(10L)
                .isbn("1234")
                .author("Maria")
                .title("Aventuras de Maria")
                .build();
    }
}
