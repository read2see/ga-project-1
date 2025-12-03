package com.acme.models;

import java.util.Optional;

public class Customer extends Person {
    private Optional<SavingsAccount> savingsAccount;
    private Optional<CheckingAccount> checkingAccount;
    private Role role;

    public Customer(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.role = Role.CUSTOMER;
        this.savingsAccount = Optional.empty();
        this.checkingAccount = Optional.empty();
    }

    public Role getRole() {
        return this.role;
    }

    public void createSavingsAccount(String password) {
        if (this.savingsAccount.isEmpty()) {
            this.savingsAccount = Optional.of(new SavingsAccount(this.getId(), password));
        }

    }

    public void createCheckingAccount(String password) {
        if (this.checkingAccount.isEmpty()) {
            this.checkingAccount = Optional.of(new CheckingAccount(this.getId(), password));
        }

    }

    public void setSavingsAccount(Optional<SavingsAccount> savingAccount) {
        this.savingsAccount = savingAccount;
    }

    public void setCheckingAccount(Optional<CheckingAccount> checkingAccount) {
        this.checkingAccount = checkingAccount;
    }

    public Optional<SavingsAccount> getSavingsAccount() {
        return this.savingsAccount;
    }

    public Optional<CheckingAccount> getCheckingAccount() {
        return this.checkingAccount;
    }

    @Override
    public String toJson() {
        return ("{\"id\": \"%s\"," +
                "\"firstName\": %s" +
                "\"lastName\": %s" +
                "\"email\": %s" +
                "\"failedLoginAttempts\": %d" +
                "\"\"" +
                "\"\"" +
                "\"\"" +
                "\"\"" +
                "}").formatted(
                        this.getId(),
                        this.getFirstName(),
                        this.getLastName(),
                        this.getEmail(),
                        this.getFailedLoginAttempts()
                );
    }
}
