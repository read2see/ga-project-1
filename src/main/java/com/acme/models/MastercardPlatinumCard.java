package com.acme.models;

import java.time.LocalDate;

public class MastercardPlatinumCard extends Card {

    private static final CardLimits LIMITS = new CardLimits("20000", "40000", "80000", "100000", "200000");

    public MastercardPlatinumCard() {
        super(LocalDate.now());
    }

    @Override
    public String getLabel() {
        return "MASTERCARD_PLATINUM";
    }

    @Override
    public CardLimits getLimits() {
        return LIMITS;
    }
}

