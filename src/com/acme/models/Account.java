package com.acme.models;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Account {
    private String encryptedPassword;
    private String accountNumber;
    private AccountType type;
    private BigDecimal balance;
    private int overdraftCount;
    private ArrayList<Card> cards;

    public Account(String accountNumber, String password, AccountType type, BigDecimal balance){
        this.accountNumber = accountNumber;
        this.encryptedPassword = password;
        this.type = type;
        this.balance = balance;
        this.cards = new ArrayList<>();
        this.overdraftCount = 0;
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
}
