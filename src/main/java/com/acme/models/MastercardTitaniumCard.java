package com.acme.models;

import java.time.LocalDate;

public class MastercardTitaniumCard extends Card {

    private static final CardLimits LIMITS = new CardLimits("10000", "20000", "40000", "100000", "200000");

    public MastercardTitaniumCard() {
        super(LocalDate.now());
    }

    @Override
    public String getLabel() {
        return "MASTERCARD_TITANIUM";
    }

    @Override
    public CardLimits getLimits() {
        return LIMITS;
    }
}

