package com.acme.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "accountType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CheckingAccount.class, name = "CHECKING"),
        @JsonSubTypes.Type(value = SavingsAccount.class, name = "SAVINGS")
})
public abstract class Account {

    private UUID accountId;
    private UUID customerId;
    private String accountNumber;
    private BigDecimal balance;
    private boolean active;
    private int overdraftCount;
    private final List<Card> cards = new ArrayList<>();

    private static final BigDecimal OVERDRAFT_FEE = new BigDecimal("35.00");
    private static final BigDecimal MAX_NEGATIVE_WITHDRAWAL = new BigDecimal("100.00");

    protected Account() { }

    protected Account(UUID customerId, String accountNumber, BigDecimal openingBalance) {
        this.accountId = UUID.randomUUID();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = openingBalance;
        this.active = true;
        this.overdraftCount = 0;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getOverdraftCount() {
        return overdraftCount;
    }

    public void setOverdraftCount(int overdraftCount) {
        this.overdraftCount = overdraftCount;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        boolean alreadyHasType = cards.stream().anyMatch(c -> c.getLabel().equals(card.getLabel()));
        if (alreadyHasType) {
            throw new IllegalArgumentException("Card type already issued for this account");
        }
        cards.add(card);
    }

    public BigDecimal deposit(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance = balance.add(amount);
        reactivateIfRecovered();
        return balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (!active) {
            throw new IllegalStateException("Account is inactive");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive");
        }
        if (balance.signum() < 0 && amount.compareTo(MAX_NEGATIVE_WITHDRAWAL) > 0) {
            throw new IllegalStateException("Cannot withdraw more than 100 while negative");
        }

        if (amount.compareTo(balance) <= 0) {
            balance = balance.subtract(amount);
            return balance;
        }

        if (overdraftCount >= 2) {
            active = false;
            throw new IllegalStateException("Account deactivated due to overdrafts");
        }

        balance = balance.subtract(amount).subtract(OVERDRAFT_FEE);
        overdraftCount++;
        if (overdraftCount >= 2) {
            active = false;
        }
        return balance;
    }

    public BigDecimal getOverdraftFee() {
        return OVERDRAFT_FEE;
    }

    public BigDecimal transferTo(Account destination, BigDecimal amount) {
        if (this.accountId.equals(destination.accountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        withdraw(amount);
        destination.deposit(amount);
        return balance;
    }

    @JsonIgnore
    public boolean isOwnAccountTransfer(Account destination) {
        return this.customerId.equals(destination.customerId);
    }

    private void reactivateIfRecovered() {
        if (balance.compareTo(BigDecimal.ZERO) >= 0 && overdraftCount > 0) {
            overdraftCount = 0;
            active = true;
        }
    }

    @JsonIgnore
    public abstract String getTypeLabel();

    public String getAccountsCardDetails() {

        StringBuilder template = new StringBuilder("\t\tAccount's issued cards:\n");
        getCards().forEach(card -> {
            template.append("\t\t- %s | %s | issue date: %s".formatted(card.getLabel(), card.getCardId(),card.getIssuedOn()));
        });

        return template.toString();
    }


}

