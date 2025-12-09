package com.acme.models;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckingAccount extends Account {

    public CheckingAccount() { }

    public CheckingAccount(UUID customerId, String accountNumber, BigDecimal openingBalance) {
        super(customerId, accountNumber, openingBalance);
    }

    @Override
    public String getTypeLabel() {
        return "CHECKING";
    }
}

