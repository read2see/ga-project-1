package com.acme.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private final Customer acu = new Customer("John", "Doe", "test@te.co", "xyz");

    @Test
    @DisplayName("Get role test")
    void getRole() {

        assertEquals(Role.CUSTOMER, acu.getRole());

    }

    @Test
    @DisplayName("Create Savings Account test")
    void createSavingsAccount() {
        acu.createSavingsAccount("xyz");
        assertInstanceOf(SavingsAccount.class, acu.getSavingsAccount());
    }

    @Test
    @DisplayName("Create Checking Account test")
    void createCheckingAccount() {
        acu.createCheckingAccount("xyz");
        assertInstanceOf(CheckingAccount.class, acu.getCheckingAccount());
    }

    @Test
    @DisplayName("Set Savings account test")
    void setSavingsAccount() {
        Optional<SavingsAccount> svaccounts = Optional.of(new SavingsAccount(acu.getId(), "xyz"));
        acu.setSavingsAccount(svaccounts);
        assertInstanceOf(SavingsAccount.class, acu.getSavingsAccount());
    }

    @Test
    @DisplayName("Set Checking account test")
    void setCheckingAccount() {
        Optional<CheckingAccount> ckaccount = Optional.of(new CheckingAccount(acu.getId(), "xyz"));
        acu.setCheckingAccount(ckaccount);
        assertInstanceOf(CheckingAccount.class, acu.getCheckingAccount());
    }

    @Test
    @DisplayName("Get Savings account test")
    void getSavingsAccount() {
        assertInstanceOf(SavingsAccount.class, acu.getSavingsAccount());
    }

    @Test
    @DisplayName("Get Checking account test")
    void getCheckingAccount() {
        assertInstanceOf(CheckingAccount.class, acu.getCheckingAccount());
    }
}