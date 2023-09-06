package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Set;


@RestController
@RequestMapping("/api")

public class TransactionController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> makeTransaction(Authentication authentication, @RequestParam String fromAccountNumber,
                                                  @RequestParam String toAccountNumber,
                                                  @RequestParam double amount,
                                                  @RequestParam String description) {

        Client client = clientService.findByEmail(authentication.getName());
        Set<Account> clientAccount = (client.getAccounts());
        Account fromAccount = accountService.findByNumber(fromAccountNumber);
        Account toAccount = accountService.findByNumber(toAccountNumber);

        if (description.isBlank()) {
            return new ResponseEntity<>("Description is empty", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("Account from number is empty", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isBlank()) {
            return new ResponseEntity<>("Account to number is empty", HttpStatus.FORBIDDEN);
        }
        // Verificar que los números de cuenta no sean iguales

        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("Account from and to numbers cannot be the same", HttpStatus.FORBIDDEN);
        }
        // Verificar si la cuenta de origen existe

        if (fromAccountNumber == null) {
            return new ResponseEntity<>("Account from number does not exist", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen pertenezca al cliente autenticado

        if (!clientAccount.stream().anyMatch(account -> account.getNumber().equals(fromAccountNumber))) {
            return new ResponseEntity<>("Account from number does not belong to authenticated client", HttpStatus.FORBIDDEN);
        }
        // Verificar si la cuenta de destino existe

        if (toAccountNumber == null) {
            return new ResponseEntity<>("Account to number does not exist", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen tenga saldo suficiente


        if (fromAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient balance in the account", HttpStatus.FORBIDDEN);
        }
        if (amount < 0) {
            return new ResponseEntity<>("Negative balances cannot be sent", HttpStatus.FORBIDDEN);
        }
        // Crear la transacción de débito asociada a la cuenta de origen

        Transaction debitTransaction = new Transaction(TransactionType.DEBIT, -amount, description, LocalDateTime.now());
        fromAccount.addTransaction(debitTransaction);


        // Crear la transacción de crédito asociada a la cuenta de destino
        Transaction creditTransaction = new Transaction(TransactionType.CREDIT, amount, description, LocalDateTime.now());
        toAccount.addTransaction(creditTransaction);


        // Actualizar los saldos de las cuentas
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        // Guardar los cambios en el repositorio de cuentas
        transactionService.saveTransaction(debitTransaction);
        transactionService.saveTransaction(creditTransaction);
        accountService.saveAccount(fromAccount);
        accountService.saveAccount(toAccount);


        return new ResponseEntity<>("Transaction completed successfully", HttpStatus.CREATED);

    }
}
