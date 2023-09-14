package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

   /* @Autowired
    private PasswordEncoder passwordEncoder;*/

    @GetMapping("/clients")
    public ResponseEntity<Object> getClients() {
        List<Client> clients = clientService.findAllClients();
        List<ClientDTO> clientsDTO = clientService.mapClientsDTO(clients);
        return new ResponseEntity<>(clientsDTO, HttpStatus.ACCEPTED);
    }

    @GetMapping("/clients/{id}")
    public ClientDTO getClientsById(@PathVariable Long id) {
        return clientService.getClientsDTOById(id);
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {
        if (firstName.isBlank()) {
            return new ResponseEntity<>("Name is empty", HttpStatus.FORBIDDEN);
        }
        if (lastName.isBlank()) {
            return new ResponseEntity<>("Lastname is empty", HttpStatus.FORBIDDEN);
        }
        if (email.isBlank()) {
            return new ResponseEntity<>("Email is empty", HttpStatus.FORBIDDEN);
        }
        if (password.isBlank()) {
            return new ResponseEntity<>("Password is empty", HttpStatus.FORBIDDEN);
        }
        if (clientService.findByEmail(email) != null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        } else {
            Client client = new Client(firstName, lastName, email, password);

            String randomNum;

            do {
                Random random = new Random();
                randomNum = "VIN-" + CardUtils.getRandomCvvNumber();
            } while (accountService.findByNumber(randomNum) != null);

            Account account = new Account(randomNum, LocalDateTime.now(), 0.0);
            client.addAccount(account);
            clientService.saveClient(client);
            accountService.saveAccount(account);

        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/clients/current")
    public ClientDTO getClientCurrent(Authentication authentication) {
        return clientService.getClientCurrent(authentication.getName());
    }

}
