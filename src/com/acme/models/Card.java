package com.acme.models;

import java.math.BigDecimal;

public class Card {

    private CardType type;

    private final BigDecimal limit = new BigDecimal("0.0");

    public CardType getType() {
        return type;
    }
}
