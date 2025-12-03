package com.acme.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public abstract class Account {
    private UUID userId;
    private boolean isActive;
    private String encryptedPassword;
    private final UUID accountNumber;
    private BigDecimal balance;
    private int overdraftCount;
    private ArrayList<CreditCard> creditCards;
    private final BigDecimal overdraftPenalty = new BigDecimal("35.0");

    public Account(UUID balance, String password) {
        this.userId = balance;
        this.accountNumber = UUID.randomUUID();
        this.encryptedPassword = password;
        this.creditCards = new ArrayList();
        this.overdraftCount = 0;
        this.isActive = true;
        this.balance = BigDecimal.ZERO;
    }

    public Account(UUID userId, String password, BigDecimal balance) {
        this.userId = userId;
        this.accountNumber = UUID.randomUUID();
        this.encryptedPassword = password;
        this.creditCards = new ArrayList();
        this.overdraftCount = 0;
        this.isActive = true;
        this.balance = balance;
    }

    public boolean addCard(CreditCard card){

        boolean isAllowed = this.creditCards.stream().filter(c -> c.getType().equals(card.getType())).toList().isEmpty();

        if(!isAllowed){
            return false;
        }

        this.creditCards.add(card);

        return true;
    }

    public boolean removeCard(CreditCard card) {
        return false;
    }

    public void setEncryptedPassword(String password) {
        this.encryptedPassword = password;
    }

    public UUID getAccountNumber() {
        return this.accountNumber;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getOverdraftCount() {
        return this.overdraftCount;
    }

    public void setOverdraftCount(int count) {
        this.overdraftCount = count;
    }

    public ArrayList<CreditCard> getCards() {
        return this.creditCards;
    }

    public void setCards(ArrayList<CreditCard> cards) {
        this.creditCards = cards;
    }

    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("-0.0")) <= 0) {
            throw new RuntimeException("Deposit amount is negative.");
        } else {
            this.balance = this.balance.add(amount);
            return this.balance;
        }
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (!this.isActive) {
            System.out.println("Your account is deactivated, please resolve your negative balance to reactivate your account");
            return this.balance;
        } else {
            if (amount.compareTo(this.balance) < 0) {
                approveWithdrawal(amount);
            } else if (overdraftCount < 2) {
                approveWithdrawal(amount);
                applyOverdraftFee();
            } else {
                this.isActive = false;
            }

            return this.balance;
        }
    }

    public BigDecimal transfer(BigDecimal amount, Account destAccount) {
        return new BigDecimal("0.0");
    }

    public void approveWithdrawal(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        new Transaction("withdraw", this, amount, null);
    }

    public void applyOverdraftFee() {
        ++this.overdraftCount;
        this.setBalance(this.getBalance().subtract(this.overdraftPenalty));
        new Transaction("overdraft", this, this.overdraftPenalty, null);
    }
}
