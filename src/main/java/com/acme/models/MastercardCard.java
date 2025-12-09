package com.acme.models;

import java.time.LocalDate;

public class MastercardCard extends Card {

    private static final CardLimits LIMITS = new CardLimits("5000", "10000", "20000", "100000", "200000");

    public MastercardCard() {
        super(LocalDate.now());
    }

    @Override
    public String getLabel() {
        return "MASTERCARD";
    }

    @Override
    public CardLimits getLimits() {
        return LIMITS;
    }
}

