package com.acme.models;

import java.math.BigDecimal;
import java.util.UUID;

public class SavingsAccount extends Account {

    public SavingsAccount() { }

    public SavingsAccount(UUID customerId, String accountNumber, BigDecimal openingBalance) {
        super(customerId, accountNumber, openingBalance);
    }

    @Override
    public String getTypeLabel() {
        return "SAVINGS";
    }
}

