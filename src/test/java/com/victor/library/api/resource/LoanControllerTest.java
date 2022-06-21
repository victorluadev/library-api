package com.victor.library.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.library.api.dto.LoanDTO;
import com.victor.library.api.dto.ReturnedLoanDTO;
import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Book;
import com.victor.library.model.entity.Loan;
import com.victor.library.service.BookService;
import com.victor.library.service.LoanService;
import org.hamcrest.Matchers;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = {LoanController.class})
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Should realize a new loan")
    public void createLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();

        Loan loan = Loan.builder()
                .id(1l)
                .customer("Fulano")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isCreated() )
                .andExpect( content().string("1") );
    }

    @Test
    @DisplayName("Should throw error if isbn is nonexistent")
    public void invalidIsbnCreateLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Should throw error if book is loaned")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", Matchers.hasSize(1)))
                .andExpect( jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Should return a book")
    public void returnBookTest() throws Exception{
        // cenário ( returned: true )
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1l).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Should return 404 when try to return a nonexistent book")
    public void returnNonexistentBookTest() throws Exception{
        // cenário ( returned: true )
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isNotFound());
    }
}
