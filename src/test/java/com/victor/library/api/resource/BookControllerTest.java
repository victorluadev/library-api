package com.victor.library.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.library.api.dto.BookDTO;
import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.service.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Should create a new book with success")
    public void createBookTest() throws Exception {

        // cenário
        BookDTO book = createBook();

        Book savedBook = createValidBook();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(book);

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificações
        mvc
            .perform(request)
            .andExpect( status().isCreated() )
            .andExpect( jsonPath("id").value(savedBook.getId()))
            .andExpect( jsonPath("title").value(savedBook.getTitle()))
            .andExpect( jsonPath("author").value(savedBook.getAuthor()))
            .andExpect( jsonPath("isbn").value(savedBook.getIsbn()))
        ;

    }

    @Test
    @DisplayName("Should throw an error if don't have sufficient information")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Should throw an error if has another book with same Isbn")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Cannot save duplicated Isbn"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Cannot save duplicated Isbn"));
    }

    private BookDTO createBook() {
        return BookDTO
                .builder()
                .author("João")
                .title("Aventuras no SN")
                .isbn("44221144")
                .build();
    }

    private Book createValidBook() {
        return Book
                .builder()
                .id(1L)
                .author("João")
                .title("Aventuras no SN")
                .isbn("44221144")
                .build();
    }
}
