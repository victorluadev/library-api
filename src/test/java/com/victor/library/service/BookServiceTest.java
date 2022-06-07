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
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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

    @Test
    @DisplayName("Should get a book by id")
    public void getBookByIdTest() {
        Long id = 1l;

        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // execução
        Optional<Book> foundBook = service.getById(id);

        // verificações
        assertThat( foundBook.isPresent() ).isTrue();
        assertThat( foundBook.get().getId() ).isEqualTo(id);
        assertThat( foundBook.get().getAuthor() ).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getTitle() ).isEqualTo(book.getTitle());
        assertThat( foundBook.get().getIsbn() ).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Should return empty when look for a book by id who doesnt exists")
    public void getInexistentBookByIdTest() {
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Book> foundBook = service.getById(id);

        // verificações
        assertThat( foundBook.isPresent() ).isFalse();
    }

    @Test
    @DisplayName("Should delete a book by id")
    public void deleteBookByIdTest() {
        Book book = createValidBook();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should return illegal argument trying to delete a null book")
    public void deleteInexistentBookTest() {
        Book book = Book.builder()
                .isbn("1234")
                .author("Maria")
                .title("Aventuras de Maria")
                .build();

        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));

        Assertions.assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cannot be null");

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Should update a book")
    public void updateBookTest() {
        // cenário
        Long id = 1l;
        Book updatingBook = Book.builder().id(1l).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        // execução
        Book book = service.update(updatingBook);

        // verificações
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Should return illegal argument trying to update a null book")
    public void updateInexistentBookTest() {
        Book book = new Book();

        Throwable exception = Assertions.catchThrowable(() -> service.update(book));

        Assertions.assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cannot be null");

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
