package com.acme.models;

import java.math.BigDecimal;

public abstract class CreditCard {
    private final BigDecimal limit = new BigDecimal("0.0");

    public abstract String getType();
}
