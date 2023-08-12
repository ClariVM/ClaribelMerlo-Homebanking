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
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository){
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

            ClientLoan clientLoan1 = new ClientLoan(400000., 60,client1,loan1);
            clientLoanRepository.save(clientLoan1);
            ClientLoan clientLoan2 = new ClientLoan(50000., 12,client1,loan2);
            clientLoanRepository.save(clientLoan2);

            ClientLoan clientLoan3 = new ClientLoan(100000., 24,client2,loan2);
            clientLoanRepository.save(clientLoan3);
            ClientLoan clientLoan4 = new ClientLoan(200000., 36,client2,loan3);
            clientLoanRepository.save(clientLoan4);
        };
}

}
