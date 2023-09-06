package com.mindhub.homebanking.services;


import com.mindhub.homebanking.models.Card;


public interface CardService {

    Card findCardByNumber(String number);

    void saveCard(Card card);
}
