package com.acme.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class Account {

    private UUID customerId;
    private boolean isActive;
    private String encryptedPassword;
    private String accountNumber;
    private AccountType type;
    private BigDecimal balance;
    private int overdraftCount;
    private ArrayList<Card> cards;
    final private BigDecimal overdraftPenalty = new BigDecimal("35.0");

    public Account(UUID customerId, String accountNumber, String password, AccountType type, BigDecimal balance){
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.encryptedPassword = password;
        this.type = type;
        this.balance = balance;
        this.cards = new ArrayList<>();
        this.overdraftCount = 0;
        this.isActive = true;
    }

    public boolean addCard(Card card){

        boolean isAllowed = this.cards.stream().filter(c -> c.getType().equals(card.getType())).toList().isEmpty();

        if(!isAllowed){
            return false;
        }

        this.cards.add(card);

        return true;
    }

    public boolean removeCard(Card card){
        return false;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getOverdraftCount() {
        return overdraftCount;
    }

    public void setOverdraftCount(int overdraftCount) {
        this.overdraftCount = overdraftCount;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public BigDecimal deposit(BigDecimal amount) {
        if(amount.compareTo(new BigDecimal("-0.0")) <= 0){
            throw new RuntimeException("Deposit amount is negative.");
        }
        balance = balance.add(amount);
        return balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) < 0 && this.isActive) {
            balance = balance.subtract(amount);
            new Transaction("withdraw", this, amount, null);

        } else if (overdraftCount < 2 && this.isActive){

            balance = balance.subtract(amount);
            new Transaction("withdraw", this, amount, null);

            overdraftCount++;
            setBalance(getBalance().subtract(overdraftPenalty));
            new Transaction("overdraft", this, overdraftPenalty, null);
        } else {
            this.isActive = false;
        }

        return balance;
    }

    public BigDecimal transfer(BigDecimal amount, Account destinationAccount){

        return new BigDecimal("0.0");
    }

}
