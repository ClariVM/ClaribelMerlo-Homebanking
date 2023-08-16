package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Set<AccountDTO> accounts = new HashSet<>();
    private Set <ClientLoanDTO> loans =new HashSet<>();
    private Set<CardDTO> cards =new HashSet<>();

    public ClientDTO(Client client) {
        id = client.getId();
        firstName = client.getFirstName();
        lastName = client.getLastName();
        email = client.getEmail();

        accounts = client.getAccounts()
                        .stream()
                        .map(account -> new AccountDTO(account))
                        .collect(Collectors.toSet());

        loans = client.getClientLoans()
                .stream()
                .map(loan -> new ClientLoanDTO(loan))
                .collect(Collectors.toSet());
        cards = client.getCards()
                .stream()
                .map(card -> new CardDTO(card))
                .collect(Collectors.toSet());

    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public Set<ClientLoanDTO> getLoans() {
        return loans;
    }

    public Set<CardDTO> getCards() {
        return cards;
    }
}
