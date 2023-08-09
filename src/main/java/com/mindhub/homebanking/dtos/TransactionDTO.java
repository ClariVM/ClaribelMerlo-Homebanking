package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDate;

public class TransactionDTO {
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date = LocalDate.now();
    private TransactionType type;

    public TransactionDTO(Transaction transaction) {
       id = transaction.getId();
       amount = transaction.getAmount();
       description = transaction.getDescription();
       date = transaction.getDate();
       type = transaction.getType();
    }

    //Getters

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }
}
