package com.acme.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    private final Customer acu = new Customer("John", "Doe", "test@te.co", "xyz");

    @Test
    @DisplayName("Add card to customer account")
    void addCard() {
        acu.createSavingsAccount("xyz");
        if(acu.getSavingsAccount().isPresent()){
            acu.getSavingsAccount().addCard();
        }
        assertEquals(1, acu.getSavingsAccount().size());
    }

    @Test
    @DisplayName("Adding duplicate card to customer account test")
    void addDuplicateCard() {
        acu.createSavingsAccount("xyz");
        acu.createSavingsAccount("xyz");
        if(acu.getSavingsAccount().isPresent()){
            acu.getSavingsAccount().addCard();
        }
        assertEquals(1, acu.getSavingsAccount().size());
    }

    @Test
    @DisplayName("Remove card from customer account")
    void removeCard() {
        acu.getSavingsAccount().getCards().get(0);
        acu.getSavingsAccount().removeCard("uuid");
    }

    @Test
    @DisplayName("Set Account Password")
    void setEncryptedPassword() {

    }

    @Test
    @DisplayName("Account number getter test")
    void getAccountNumber() {

    }

    @Test
    @DisplayName("Balance getter test")
    void getBalance() {

    }

    @Test
    @DisplayName("Balance setter test")
    void setBalance() {

    }

    @Test
    @DisplayName("Overdraft getter test")
    void getOverdraftCount() {

    }

    @Test
    @DisplayName("Setting overdraft count test")
    void setOverdraftCount() {

    }

    @Test
    @DisplayName("Get account cards test")
    void getCards() {

    }

    @Test
    @DisplayName("Set account cards test")
    void setCards() {

    }

    @Test
    @DisplayName("Deposit operation test")
    void deposit() {

    }

    @Test
    @DisplayName("Withdraw operation test")
    void withdraw() {

    }

    @Test
    @DisplayName("Transfer operation test")
    void transfer() {

    }

    @Test
    @DisplayName("Approve Withdrawal test")
    void approveWithdrawal() {

    }

    @Test
    @DisplayName("Apply Overdraft fee test")
    void applyOverdraftFee() {

    }

}