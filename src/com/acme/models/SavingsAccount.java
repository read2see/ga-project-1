package com.acme.models;

import java.math.BigDecimal;
import java.util.UUID;

public class SavingsAccount extends Account {
    private final String type = "SavingsAccount";

    public SavingsAccount(UUID userId, String password) {
        super(userId, password);
    }

    public SavingsAccount(UUID userId, String password, BigDecimal balance) {
        super(userId, password, balance);
    }
}
