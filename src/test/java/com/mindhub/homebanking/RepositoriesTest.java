package com.mindhub.homebanking;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class RepositoriesTest {
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Test
    public void existLoans() {

        List<Loan> loans = loanRepository.findAll();

        assertThat(loans, is(not(empty())));
    }

    @Test
    public void existPersonalLoan() {

        List<Loan> loans = loanRepository.findAll();

        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));

    }

    @Test
    public void existClients() {

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, is(not(empty())));

    }

    @Test
    public void existAdminClient() {

        List<Client> clients = clientRepository.findAll();

        assertThat(clients, hasItem(hasProperty("email", equalTo("admin@admin.com"))));
    }

    @Test
    public void existAccounts() {

        List<Account> accounts = accountRepository.findAll();

        assertThat(accounts, is(not(empty())));

    }

    @Test
    public void allAccountsAreAssociatedToClient() {

        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            Set<Account> clientsAccounts = account.getClient().getAccounts();
            assertThat(clientsAccounts, is(not(empty())));
        }

    }

    @Test
    public void existCards() {

        List<Card> cards = cardRepository.findAll();

        assertThat(cards, is(not(empty())));

    }

    @Test
    public void eachClientHasMaximumSixCards() {

        List<Card> cards = cardRepository.findAll();

        for (Card card : cards) {
            Set<Card> clientsCards = card.getClient().getCards();
            assertThat(clientsCards.size(), lessThanOrEqualTo(6));

        }
    }

    @Test
    public void existTransaction() {

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions, is(not(empty())));

    }

}

