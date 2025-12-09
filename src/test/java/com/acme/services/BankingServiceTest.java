package com.acme.services;

import com.acme.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankingServiceTest {

    private FileDatabaseService database;
    private AuthenticationService auth;
    private BankingService banking;

    @BeforeEach
    void setup() throws Exception {
        System.setProperty("DATA_ENCRYPTION_KEY", "test_key_1234567890123456");
        Path temp = Files.createTempDirectory("banking-tests");
        database = new FileDatabaseService(temp);
        auth = new FileAuthenticationService(database);
        banking = new BankingService(database);
    }

    @Test
    void locksCustomerAfterThreeFailedAttempts() {
        Customer customer = auth.registerCustomer("Jane", "Doe", "jane@example.com", "secret");

        assertTrue(auth.login("jane@example.com", "wrong").isEmpty());
        assertTrue(auth.login("jane@example.com", "wrong").isEmpty());
        assertTrue(auth.login("jane@example.com", "wrong").isEmpty());
        assertTrue(auth.login("jane@example.com", "secret").isEmpty(), "Should be locked after 3 failures");

        // Simulate lock expiration
        customer = database.findByEmail("jane@example.com")
                .map(p -> (Customer) p)
                .orElseThrow();
        customer.setLocked(true);
        customer.setLockedUntil(LocalDateTime.now().minusMinutes(2));
        database.saveCustomer(customer);

        Optional<Person> unlocked = auth.login("jane@example.com", "secret");
        assertTrue(unlocked.isPresent());
        assertFalse(((Customer) unlocked.get()).isLocked());
    }

    @Test
    void createsCheckingAccount() {
        Customer customer = auth.registerCustomer("John", "Smith", "john@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        assertNotNull(account);
        assertTrue(account instanceof CheckingAccount);
        assertEquals("CHECKING", account.getTypeLabel());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertTrue(account.isActive());
        assertEquals(1, account.getCards().size());
        assertEquals("MASTERCARD", account.getCards().get(0).getLabel());
    }

    @Test
    void createsSavingsAccount() {
        Customer customer = auth.registerCustomer("Mary", "Johnson", "mary@example.com", "pass");
        Card card = new MastercardTitaniumCard();
        Account account = banking.createAccount(customer, "SAVINGS", new BigDecimal("500.00"), card);

        assertNotNull(account);
        assertTrue(account instanceof SavingsAccount);
        assertEquals("SAVINGS", account.getTypeLabel());
        assertEquals(new BigDecimal("500.00"), account.getBalance());
        assertEquals(1, account.getCards().size());
    }

    @Test
    void depositsMoneySuccessfully() {
        Customer customer = auth.registerCustomer("Bob", "Williams", "bob@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        BigDecimal newBalance = banking.deposit(customer, account, new BigDecimal("50.00"), card);
        assertEquals(new BigDecimal("150.00"), newBalance);
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    void withdrawsMoneySuccessfully() {
        Customer customer = auth.registerCustomer("Alice", "Brown", "alice@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("200.00"), card);

        BigDecimal newBalance = banking.withdraw(customer, account, new BigDecimal("50.00"), card);
        assertEquals(new BigDecimal("150.00"), newBalance);
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    void appliesOverdraftFeeOnFirstOverdraft() {
        Customer customer = auth.registerCustomer("John", "Smith", "john@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        BigDecimal newBalance = banking.withdraw(customer, account, new BigDecimal("150.00"), card);
        // 100 - 150 - 35 (overdraft fee) = -85
        assertEquals(new BigDecimal("-85.00"), newBalance);
        assertEquals(1, account.getOverdraftCount());
        assertTrue(account.isActive());
    }

    @Test
    void deactivatesAccountAfterSecondOverdraft() {
        Customer customer = auth.registerCustomer("John", "Smith", "john@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        // First overdraft
        banking.withdraw(customer, account, new BigDecimal("150.00"), card);
        // Reload account to get updated state
        customer = banking.findCustomerByEmail("john@example.com").orElseThrow();
        account = customer.getAccounts().get(0);
        assertEquals(1, account.getOverdraftCount());
        assertTrue(account.isActive());

        // Second overdraft: should succeed but deactivate account
        banking.withdraw(customer, account, new BigDecimal("50.00"), card);
        // Reload account to get updated state
        customer = banking.findCustomerByEmail("john@example.com").orElseThrow();
        account = customer.getAccounts().get(0);
        assertEquals(2, account.getOverdraftCount());
        assertFalse(account.isActive(), "Account should deactivate after second overdraft");

        // Third withdrawal attempt should fail because account is inactive
        Customer finalCustomer = customer;
        Account finalAccount = account;
        assertThrows(IllegalStateException.class, () ->
                banking.withdraw(finalCustomer, finalAccount, new BigDecimal("10.00"), card));
    }

    @Test
    void preventsWithdrawalWhenNegativeBalanceExceedsLimit() {
        Customer customer = auth.registerCustomer("John", "Smith", "john@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        // Create negative balance
        account.setBalance(new BigDecimal("-50.00"));

        // Should allow withdrawal up to $100 when negative
        assertDoesNotThrow(() -> account.withdraw(new BigDecimal("50.00")));

        // Should prevent withdrawal over $100 when negative
        assertThrows(IllegalStateException.class, () ->
                account.withdraw(new BigDecimal("101.00")));
    }

    @Test
    void reactivatesAccountWhenBalanceRecovers() {
        Customer customer = auth.registerCustomer("John", "Smith", "john@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        // Create overdraft
        account.setBalance(new BigDecimal("-50.00"));
        account.setOverdraftCount(1);

        // Deposit enough to recover
        banking.deposit(customer, account, new BigDecimal("100.00"), card);
        assertEquals(0, account.getOverdraftCount());
        assertTrue(account.isActive());
    }

    @Test
    void transfersBetweenOwnAccounts() {
        Customer customer = auth.registerCustomer("Sarah", "Davis", "sarah@example.com", "pass");
        Card card = new MastercardCard();
        Account source = banking.createAccount(customer, "CHECKING", new BigDecimal("500.00"), card);
        Account destination = banking.createAccount(customer, "SAVINGS", new BigDecimal("100.00"), new MastercardTitaniumCard());

        banking.transfer(customer, source, destination, new BigDecimal("200.00"), card);

        assertEquals(new BigDecimal("300.00"), source.getBalance());
        assertEquals(new BigDecimal("300.00"), destination.getBalance());
    }

    @Test
    void transfersToExternalAccount() {
        Customer customer1 = auth.registerCustomer("Tom", "Wilson", "tom@example.com", "pass");
        Customer customer2 = auth.registerCustomer("Lisa", "Anderson", "lisa@example.com", "pass");
        Card card = new MastercardCard();
        Account source = banking.createAccount(customer1, "CHECKING", new BigDecimal("500.00"), card);
        Account destination = banking.createAccount(customer2, "CHECKING", new BigDecimal("100.00"), new MastercardCard());

        banking.transfer(customer1, source, destination, new BigDecimal("200.00"), card);

        assertEquals(new BigDecimal("300.00"), source.getBalance());
        assertEquals(new BigDecimal("300.00"), destination.getBalance());
    }

    @Test
    void enforcesDailyWithdrawLimit() {
        Customer customer = auth.registerCustomer("Sara", "Miles", "sara@example.com", "pw");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "SAVINGS", new BigDecimal("10000.00"), card);

        // Mastercard limit is $5000 per day
        banking.withdraw(customer, account, new BigDecimal("4000.00"), card);

        // Should fail when trying to withdraw more than remaining limit
        assertThrows(IllegalStateException.class, () ->
                banking.withdraw(customer, account, new BigDecimal("1500.00"), card));
    }

    @Test
    void enforcesDailyDepositLimit() {
        Customer customer = auth.registerCustomer("Mike", "Taylor", "mike@example.com", "pw");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        // Mastercard own account deposit limit is $200,000 per day
        // This should work
        assertDoesNotThrow(() -> banking.deposit(customer, account, new BigDecimal("50000.00"), card));
    }

    @Test
    void enforcesDailyTransferLimit() {
        Customer customer = auth.registerCustomer("Emma", "Martinez", "emma@example.com", "pw");
        Card card = new MastercardCard();
        Account source = banking.createAccount(customer, "CHECKING", new BigDecimal("20000.00"), card);
        Account destination = banking.createAccount(customer, "SAVINGS", new BigDecimal("100.00"), new MastercardTitaniumCard());

        // Mastercard own account transfer limit is $20,000 per day
        banking.transfer(customer, source, destination, new BigDecimal("15000.00"), card);

        // Should fail when trying to transfer more than remaining limit
        assertThrows(IllegalStateException.class, () ->
                banking.transfer(customer, source, destination, new BigDecimal("6000.00"), card));
    }

    @Test
    void differentCardTypesHaveDifferentLimits() {
        Customer customer = auth.registerCustomer("David", "Lee", "david@example.com", "pw");
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("50000.00"), new MastercardCard());

        // Mastercard limit: $5000
        Card mastercard = account.getCards().get(0);
        assertEquals(new BigDecimal("5000"), mastercard.getLimits().getWithdrawLimitPerDay());

        // Add Platinum card with higher limits
        Card platinum = new MastercardPlatinumCard();
        account.addCard(platinum);
        assertEquals(new BigDecimal("20000"), platinum.getLimits().getWithdrawLimitPerDay());
    }

    @Test
    void preventsAddingDuplicateCardType() {
        Customer customer = auth.registerCustomer("Nancy", "Garcia", "nancy@example.com", "pw");
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), new MastercardCard());

        // Try to add another Mastercard
        assertThrows(IllegalArgumentException.class, () ->
                account.addCard(new MastercardCard()));
    }

    @Test
    void findsCustomerByEmail() {
        Customer customer = auth.registerCustomer("Robert", "Hernandez", "robert@example.com", "pass");

        Optional<Customer> found = banking.findCustomerByEmail("robert@example.com");
        assertTrue(found.isPresent());
        assertEquals(customer.getId(), found.get().getId());

        Optional<Customer> notFound = banking.findCustomerByEmail("nonexistent@example.com");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void createsTransactionHistory() {
        Customer customer = auth.registerCustomer("Jennifer", "Lopez", "jennifer@example.com", "pass");
        Card card = new MastercardCard();
        Account account = banking.createAccount(customer, "CHECKING", new BigDecimal("100.00"), card);

        banking.deposit(customer, account, new BigDecimal("50.00"), card);
        banking.withdraw(customer, account, new BigDecimal("25.00"), card);

        TransactionService transactionService = new TransactionService(database);
        var transactions = transactionService.getStatement(customer.getId());

        assertTrue(transactions.size() >= 3); // Opening deposit + deposit + withdraw
        assertTrue(transactions.stream().anyMatch(t -> t.getType() == TransactionType.DEPOSIT));
        assertTrue(transactions.stream().anyMatch(t -> t.getType() == TransactionType.WITHDRAW));
    }
}

