package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> findAllLoans();

    List<LoanDTO> mapClientsDTO(List<Loan> loans);

    Loan findById(Long id);
}
