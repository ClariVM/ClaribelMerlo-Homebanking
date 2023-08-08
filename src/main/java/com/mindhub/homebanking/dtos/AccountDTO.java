package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;

import java.time.LocalDate;

public class AccountDTO {
    private Long id;
    private String number;
    private LocalDate creationDate = LocalDate.now();
    private Double balance;


    public AccountDTO(Account account){
       id = account.getId();
       number =account.getNumber();
       creationDate = account.getCreationDate();
       balance = account.getBalance();


    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return creationDate;
    }

    public Double getBalance() {
        return balance;
    }
}
