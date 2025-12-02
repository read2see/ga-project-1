package com.acme.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Transaction {

    // Transaction descriptions
    // withdraw
    // deposit
    // transfer
    // overdraft

    static HashMap<String, String> descriptionsTemplates = new HashMap<>(Map.of(
            "withdraw", "withdrew %s",
            "deposit", "deposited %s",
            "transfer", "transferred %s to account no. %s",
            "overdraft", "overdraft charge of %s"
        ));

    private String accountNumber;
    private String description;
    private LocalDateTime createdAt;
    private String type;
    private BigDecimal amount;
    private BigDecimal postBalance;

    public Transaction(String type, Account originalAccount, BigDecimal amount, Optional<Account> destAccount) {
        this.accountNumber = originalAccount.getAccountNumber();
        this.description = getDetailedDescription(type, amount, destAccount);
        this.createdAt = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.postBalance = originalAccount.getBalance();
    }

    public String getDetailedDescription(String type, BigDecimal amount, Optional<Account> destAccount){

        if(destAccount.isPresent()){
            return descriptionsTemplates.get(type).formatted(amount, destAccount);
        }

        return descriptionsTemplates.get(type).formatted(amount);
    }
}
