package com.victor.library.service.impl;

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
        return repository.save(loan);
    }
}
