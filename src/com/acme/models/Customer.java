package com.acme.models;

import java.util.Optional;

public class Customer extends Person {
    private Optional<SavingsAccount> savingsAccount;
    private Optional<CheckingAccount> checkingAccount;
    private Role role;

    public Customer(String var1, String var2, String var3, String var4) {
        super(var1, var2, var3, var4);
        this.role = Role.CUSTOMER;
        this.savingsAccount = Optional.empty();
        this.checkingAccount = Optional.empty();
    }

    public Role getRole() {
        return this.role;
    }

    public void createSavingsAccount(String var1) {
        if (this.savingsAccount.isEmpty()) {
            this.savingsAccount = Optional.of(new SavingsAccount(this.getId(), var1));
        }

    }

    public void createCheckingAccount(String var1) {
        if (this.checkingAccount.isEmpty()) {
            this.checkingAccount = Optional.of(new CheckingAccount(this.getId(), var1));
        }

    }

    public void setSavingsAccount(Optional<SavingsAccount> var1) {
        this.savingsAccount = var1;
    }

    public void setCheckingAccount(Optional<CheckingAccount> var1) {
        this.checkingAccount = var1;
    }

    public Optional<SavingsAccount> getSavingsAccount() {
        return this.savingsAccount;
    }

    public Optional<CheckingAccount> getCheckingAccount() {
        return this.checkingAccount;
    }

    @Override
    public String toJson() {
        return "{\"id\": \"%s\"}".formatted(this.getId());
    }
}
