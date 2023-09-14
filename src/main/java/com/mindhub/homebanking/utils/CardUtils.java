package com.mindhub.homebanking.utils;

import java.util.Random;

public final class CardUtils {

    public static String getCardNumber() {
        String randomCardNumber;
        Random random = new Random();
        randomCardNumber = random.nextInt(9999) + " " + random.nextInt(9999) + " " + random.nextInt(9999) + " " + random.nextInt(9999);
        return randomCardNumber;
    }

    public static int getRandomCvvNumber() {
        int randomCvvNumber = new Random().nextInt(1000);
        return randomCvvNumber;
    }
}
