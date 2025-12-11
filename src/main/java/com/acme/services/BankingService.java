package com.acme.services;

import com.acme.models.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankingService {

    private final FileDatabaseService databaseService;

    public BankingService(FileDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public Account createAccount(Customer customer, String accountType, BigDecimal openingBalance, Card card) {
        Account account = instantiateAccount(customer.getId(), accountType, openingBalance);
        account.addCard(card);
        customer.addAccount(account);
        databaseService.saveCustomer(customer);
        databaseService.appendTransaction(customer.getId(),
                new Transaction(TransactionType.DEPOSIT, account, openingBalance, Optional.empty(), card.getLabel()));
        return account;
    }

    public void issueNewCard(Customer customer,Account account, Card card) {
        account.addCard(card);
        databaseService.saveCustomer(customer);
    }

    public BigDecimal deposit(Customer customer, Account account, BigDecimal amount, Card card) {
        enforceLimit(customer, TransactionType.DEPOSIT, card, amount, true);
        account.deposit(amount);
        persistCustomerAccount(customer, account);
        databaseService.appendTransaction(customer.getId(),
                new Transaction(TransactionType.DEPOSIT, account, amount, Optional.empty(), card.getLabel()));
        return account.getBalance();
    }

    public BigDecimal withdraw(Customer customer, Account account, BigDecimal amount, Card card) {
        enforceLimit(customer, TransactionType.WITHDRAW, card, amount, true);
        BigDecimal before = account.getBalance();
        account.withdraw(amount);
        persistCustomerAccount(customer, account);
        databaseService.appendTransaction(customer.getId(),
                new Transaction(TransactionType.WITHDRAW, account, amount, Optional.empty(), card.getLabel()));
        if (amount.compareTo(before) > 0) {
            databaseService.appendTransaction(customer.getId(),
                    new Transaction(TransactionType.OVERDRAFT_FEE, account, account.getOverdraftFee(), Optional.empty(), card.getLabel()));
        }
        return account.getBalance();
    }

    public void transfer(Customer owner, Account source, Account destination, BigDecimal amount, Card card) {
        boolean ownAccount = source.isOwnAccountTransfer(destination);
        enforceLimit(owner, TransactionType.TRANSFER, card, amount, ownAccount);
        source.transferTo(destination, amount);
        persistCustomerAccount(owner, source);

        if (!ownAccount) {
            // destination belongs to another customer
            databaseService.findCustomerById(destination.getCustomerId())
                    .ifPresent(destinationOwner -> {
                        persistCustomerAccount(destinationOwner, destination);
                        databaseService.saveCustomer(destinationOwner);
                    });
        } else {
            persistCustomerAccount(owner, destination);
        }

        databaseService.appendTransaction(owner.getId(),
                new Transaction(TransactionType.TRANSFER, source, amount, Optional.of(destination), card.getLabel()));
        databaseService.appendTransaction(destination.getCustomerId(),
                new Transaction(TransactionType.DEPOSIT, destination, amount, Optional.of(source), card.getLabel()));
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return databaseService.findByEmail(email)
                .filter(p -> p instanceof Customer)
                .map(p -> (Customer) p);
    }

    private void enforceLimit(Customer customer,
                              TransactionType type,
                              Card card,
                              BigDecimal amount,
                              boolean ownAccount) {
        CardLimits limits = card.getLimits();
        BigDecimal limit = switch (type) {
            case WITHDRAW -> limits.getWithdrawLimitPerDay();
            case TRANSFER -> ownAccount ? limits.getTransferOwnLimitPerDay() : limits.getTransferLimitPerDay();
            case DEPOSIT -> ownAccount ? limits.getDepositOwnLimitPerDay() : limits.getDepositLimitPerDay();
            case OVERDRAFT_FEE -> new BigDecimal("0.0");
        };

        if (limit.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal todaysTotal = databaseService.loadTransactions(customer.getId()).stream()
                .filter(tx -> tx.getCardLabel() != null && tx.getCardLabel().equals(card.getLabel()))
                .filter(tx -> tx.getType() == type)
                .filter(tx -> tx.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (todaysTotal.add(amount).compareTo(limit) > 0) {
            throw new IllegalStateException("Daily limit exceeded for " + card.getLabel());
        }
    }

    private void persistCustomerAccount(Customer customer, Account updatedAccount) {
        List<Account> accounts = customer.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountId().equals(updatedAccount.getAccountId())) {
                accounts.set(i, updatedAccount);
            }
        }
        databaseService.saveCustomer(customer);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private Account instantiateAccount(UUID customerId, String accountType, BigDecimal openingBalance) {
        return switch (accountType.toUpperCase()) {
            case "CHECKING" -> new CheckingAccount(customerId, generateAccountNumber(), openingBalance);
            case "SAVINGS" -> new SavingsAccount(customerId, generateAccountNumber(), openingBalance);
            default -> throw new IllegalArgumentException("Unknown account type: " + accountType);
        };
    }

}

