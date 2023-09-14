package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/accounts")
    public ResponseEntity<Object> getAccounts() {
        List<Account> accounts = accountService.findAllAccounts();
        List<AccountDTO> accountsDTO = accountService.mapClientsDTO(accounts);
        return new ResponseEntity<>(accountsDTO, HttpStatus.ACCEPTED);
    }


    @GetMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccountsById(@PathVariable Long id, Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());
        Account account = accountService.findById(id);
        if (account == null) {
            return new ResponseEntity<>("account not found", HttpStatus.BAD_GATEWAY);
        }
        if (account.getClient().equals(client)) {
            AccountDTO accountDTO = new AccountDTO(account);
            return new ResponseEntity<>(accountDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("This account it's not your", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());
        if (client.getAccounts().size() == 3) {
            return new ResponseEntity<>("Max amount of accounts reached", HttpStatus.FORBIDDEN);
        }
        String randomNum;
        do {
            Random random = new Random();
            randomNum = "VIN-" + random.nextInt(90000000);
        } while (accountService.findByNumber(randomNum) != null);

        Account account = new Account(randomNum, LocalDateTime.now(), 0.0);
        client.addAccount(account);
        accountService.saveAccount(account);
        return new ResponseEntity<>("Account created succesfully", HttpStatus.CREATED);
    }

    @GetMapping("/clients/current/accounts")
    public ResponseEntity<Object> getAccount(Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        if (client != null) {
            return new ResponseEntity<>(new ClientDTO(client).getAccounts(), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Resource bot found", HttpStatus.NOT_FOUND);
        }
    }

}





