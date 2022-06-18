package com.victor.library.service.impl;

import com.victor.library.exception.BusinessException;
import com.victor.library.model.entity.Loan;
import com.victor.library.model.repository.LoanRepository;
import com.victor.library.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }
}
