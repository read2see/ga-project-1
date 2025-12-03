package com.acme.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Transaction {
    static HashMap<String, String> descriptionsTemplates = new HashMap(Map.of("withdraw", "withdrew %s", "deposit", "deposited %s", "transfer", "transferred %s to account no. %s", "overdraft", "overdraft charge of %s"));
    private UUID accountNumber;
    private String description;
    private LocalDateTime createdAt;
    private String type;
    private BigDecimal amount;
    private BigDecimal postBalance;
    private Optional<Account> destAccount;

    public Transaction(String var1, Account var2, BigDecimal var3, Optional<Account> var4) {
        this.accountNumber = var2.getAccountNumber();
        this.description = this.getDetailedDescription(var1, var3, var4);
        this.createdAt = LocalDateTime.now();
        this.type = var1;
        this.amount = var3;
        this.postBalance = var2.getBalance();
        if (var4.isPresent()) {
            this.destAccount = var4;
        }

    }

    public String getDetailedDescription(String var1, BigDecimal var2, Optional<Account> var3) {
        return var3.isPresent() ? ((String)descriptionsTemplates.get(var1)).formatted(var2, var3) : ((String)descriptionsTemplates.get(var1)).formatted(var2);
    }
}
