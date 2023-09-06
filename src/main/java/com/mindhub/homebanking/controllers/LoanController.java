package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/loans")
    public ResponseEntity<Object> getLoans(Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        if (client != null) {
            List<Loan> loans = loanService.findAllLoans();
            List<LoanDTO> loansDTO = loanService.mapClientsDTO(loans);
            return new ResponseEntity<>(loansDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Don't have permission", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createClientLoans(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO) {

        if (loanApplicationDTO.getLoanId() == null) {
            return new ResponseEntity<>("Invalid Id", HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.getAmount() <= 10000) {
            return new ResponseEntity<>("Amount can't be less than 10000", HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.getPayments() <= 0) {
            return new ResponseEntity<>("Payments can't be less than zero", HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.getToAccountNumber().isBlank()) {
            return new ResponseEntity<>("account number is empty", HttpStatus.BAD_REQUEST);
        }
        // Obtener el cliente autenticado
        Client client = clientService.findByEmail(authentication.getName());
        // Obtener el préstamo solicitado por su ID
        Loan loan = loanService.findById(loanApplicationDTO.getLoanId());
        // Obtener la cuenta de destino por su número
        Account accountTo = accountService.findByNumber(loanApplicationDTO.getToAccountNumber());
        // Verificar y validar los datos de la solicitud
        if (loan == null) {
            return new ResponseEntity<>("loan not found", HttpStatus.FORBIDDEN);
        }
        // Verificar que el monto solicitado no exceda el monto máximo del préstamo
        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Requested amount exceeds maximum allowed", HttpStatus.FORBIDDEN);
        }
        //Verifica que la cantidad de cuotas se encuentre entre las disponibles del préstamo
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("Invalid number of payments", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de destino exista
        if (accountTo == null) {
            return new ResponseEntity<>("Account from number does not exist", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen pertenezca al cliente autenticado
        if (!client.getAccounts().contains(accountTo)) {
            return new ResponseEntity<>("Account from number does not belong to authenticated client", HttpStatus.FORBIDDEN);
        }
        ///////////////////////////////////////////////////
        List<Loan> loans = client.getLoans();
        int loanLimit;

        if (loan.getId() == 1 || loan.getId() == 2 || loan.getId() == 3) {
            loanLimit = 1;

            long loanSameType = loans.stream()
                    .filter(newLoan -> newLoan.getId().equals(loan.getId()))
                    .count();
            if (loanSameType >= loanLimit) {
                return new ResponseEntity<>("Maximum " + loan.getName() + " loan limit reached", HttpStatus.FORBIDDEN);
            }
        }
        /////////////////////////////////////////////////////////
        // Crear una solicitud de préstamo con el monto solicitado más el 20%
        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount() * 1.2, loanApplicationDTO.getPayments());

        //crear una transacción “CREDIT” asociada a la cuenta de destino (monto positivo) con la descripción concatenando el nombre del préstamo y la frase “loan approved”
        Transaction loanTransaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " " + "loan approved", LocalDateTime.now());

        accountTo.setBalance(accountTo.getBalance() + loanApplicationDTO.getAmount());


        // Actualizar
        client.addClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);
        accountTo.addTransaction(loanTransaction);
        clientLoanService.saveClientLoan(clientLoan);
        transactionService.saveTransaction(loanTransaction);


        return new ResponseEntity<>("Transaction completed successfully", HttpStatus.CREATED);
    }

}
