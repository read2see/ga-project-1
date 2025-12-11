package com.acme.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customer extends Person {

    private final List<Account> accounts = new ArrayList<>();
    private boolean locked;
    private int failedLoginAttempts;
    private LocalDateTime lockedUntil;

    public Customer() { }

    public Customer(String firstName, String lastName, String email, String password) {
        super(Role.CUSTOMER, firstName, lastName, email, password);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public void incrementFailedAttempts(int threshold, Duration lockDuration) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= threshold) {
            locked = true;
            lockedUntil = LocalDateTime.now().plus(lockDuration);
        }
    }

    public void resetFailedAttempts() {
        failedLoginAttempts = 0;
        locked = false;
        lockedUntil = null;
    }

    public boolean shouldUnlock() {
        if (locked && lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            resetFailedAttempts();
            return true;
        }
        return false;
    }

    @JsonIgnore
    public Account findAccount(UUID accountId) {
        return accounts.stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public String getAccountsDetails() {

        StringBuilder template = new StringBuilder();

        if(accounts.isEmpty()) {
            template.append("No accounts created yet.\n");
        } else {
            template.append("My Accounts:\n");
        }

        accounts.forEach(account -> {
            template.append("\t- %s| %s | Balance: %s\n%s".formatted(
                    account.getAccountNumber(),
                    account.getTypeLabel(),
                    account.getBalance(),
                    account.getAccountsCardDetails()
            ));
        });

        return template.toString();
    }
}

