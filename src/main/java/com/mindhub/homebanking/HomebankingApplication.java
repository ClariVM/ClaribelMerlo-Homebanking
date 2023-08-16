package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {

        SpringApplication.run(HomebankingApplication.class, args);
	}
@Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
        return (args) -> {
            Client client1 = new Client("Melba","Morel","melba@mindhub.com");
            clientRepository.save(client1);

            Client client2 = new Client("Juan","Perez","juanperez@mindhub.com");
            clientRepository.save(client2);


            Account account1 = new Account("VIN001", LocalDate.now(),5000.0);
            client1.addAccount(account1);
            accountRepository.save(account1);

            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1),7500.0);
            client1.addAccount(account2);
            accountRepository.save(account2);

            Account account3 = new Account("VIN003", LocalDate.now(),2000.0);
            client2.addAccount(account3);
            accountRepository.save(account3);

            Account account4 = new Account("VIN004", LocalDate.now().plusDays(1),4500.0);
            client2.addAccount(account4);
            accountRepository.save(account4);

            Transaction transaction1 = new Transaction(TransactionType.DEBIT,-200.,"Purchase at a store",LocalDate.now());
            account1.addTransaction(transaction1);
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction(TransactionType.CREDIT,500.0,"Refund",LocalDate.now());
            account1.addTransaction(transaction2);
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction(TransactionType.CREDIT,40000.0,"Online transfer",LocalDate.now());
            account2.addTransaction(transaction3);
            transactionRepository.save(transaction3);

            Transaction transaction4 = new Transaction(TransactionType.DEBIT,-2500.0,"Mobile Payment",LocalDate.now());
            account2.addTransaction(transaction4);
            transactionRepository.save(transaction4);

            Loan loan1 = new Loan("Hipotecario", 500000., List.of(12,24,36,48,60));
            loanRepository.save(loan1);

            Loan loan2 = new Loan("Personal", 100000., List.of(6,12,24));
            loanRepository.save(loan2);

            Loan loan3 = new Loan("Automotriz", 300000., List.of(6,12,24,36));
            loanRepository.save(loan3);

            ClientLoan clientLoan1 = new ClientLoan(400000., 60);
            client1.addClientLoan(clientLoan1);
            loan1.addClientLoan(clientLoan1);
            clientLoanRepository.save(clientLoan1);

            ClientLoan clientLoan2 = new ClientLoan(50000., 12);
            client1.addClientLoan(clientLoan2);
            loan2.addClientLoan(clientLoan2);
            clientLoanRepository.save(clientLoan2);

            ClientLoan clientLoan3 = new ClientLoan(100000., 24);
            client2.addClientLoan(clientLoan3);
            loan1.addClientLoan(clientLoan3);
            clientLoanRepository.save(clientLoan3);

            ClientLoan clientLoan4 = new ClientLoan(200000., 36);
            client2.addClientLoan(clientLoan4);
            loan3.addClientLoan(clientLoan4);
            clientLoanRepository.save(clientLoan4);

            //Una tarjeta de débito GOLD para el cliente Melba, la fecha de inicio de validez es la fecha actual y la fecha de vencimiento 5 años desde la fecha actual, cardholder tendrá el nombre y apellido del cliente concatenado, los demás campos los puedes completar a tu elección, recuerda que el cvv tiene solo 3 dígitos.
            //
            //Una tarjeta de crédito Titanium para el cliente Melba con los mismos datos excepto número y cvv.
            //
            //Crea una tarjeta de crédito silver para el segundo cliente.
            Card card1 = new Card(client1.getFirstName()+" "+client1.getLastName(), CardType.DEBIT,CardColor.GOLD,"2344-2323-2344-2344",234,LocalDate.now(),LocalDate.now().plusYears(5));
            client1.addCard(card1);
            cardRepository.save(card1);

            Card card2 = new Card(client1.getFirstName()+" "+client1.getLastName(), CardType.CREDIT,CardColor.TITANIUM,"111-2222-3333-4444",111,LocalDate.now(),LocalDate.now().plusYears(5));
            client1.addCard(card2);
            cardRepository.save(card2);

            Card card3 = new Card(client2.getFirstName()+" "+client2.getLastName(), CardType.CREDIT,CardColor.SILVER,"111-2222-3333-4444",111,LocalDate.now(),LocalDate.now().plusYears(5));
            client2.addCard(card3);
            cardRepository.save(card3);
        };
}

}
