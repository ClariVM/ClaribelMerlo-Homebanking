package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {
        Client client = clientService.findByEmail(authentication.getName());

        if (cardType == null || cardColor == null) {
            return new ResponseEntity<>("Missing data", HttpStatus.NO_CONTENT);
        }

        //client.getCards() me trae solo las tarjetas del cliente autenticado, si quisiera el Objeto deberia usar el repositorio
        Set<Card> cards = client.getCards();

        int cardLimit;
        if (cardType == CardType.CREDIT || cardType == CardType.DEBIT) {
            cardLimit = 3;

            long cardsSameType = cards.stream()
                    .filter(newCard -> newCard.getType() == cardType)
                    .count();
            if (cardsSameType >= cardLimit) {
                return new ResponseEntity<>("Max amount of card reached", HttpStatus.FORBIDDEN);
            }
            boolean colorUsed = cards.stream()
                    .anyMatch(card -> card.getType() == cardType && card.getColor() == cardColor);
            if (colorUsed) {
                return new ResponseEntity<>("Color already used", HttpStatus.FORBIDDEN);
            }
        }
        // Generar número de tarjeta aleatorio y único

        String randomCardNumber;
        do {
            randomCardNumber = CardUtils.getCardNumber();
        } while (cardService.findCardByNumber(randomCardNumber) != null);
        int randomCvvNumber = CardUtils.getRandomCvvNumber();


        Card card = new Card(client.getFirstName(), cardType, cardColor, randomCardNumber, randomCvvNumber, LocalDateTime.now(), LocalDateTime.now().plusYears(5));
        client.addCard(card);
        cardService.saveCard(card);
        return new ResponseEntity<>("Account created succesfully", HttpStatus.CREATED);
    }




}

