package com.acme.models;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckingAccount extends Account {
    private final String type = "CheckingAccount";

    public CheckingAccount(UUID var1, String var2) {
        super(var1, var2);
    }

    public CheckingAccount(UUID var1, String var2, BigDecimal var3) {
        super(var1, var2, var3);
    }
}
