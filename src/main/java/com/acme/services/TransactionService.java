package com.acme.services;

import com.acme.models.Transaction;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionService {

    private final FileDatabaseService databaseService;

    public TransactionService(FileDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public List<Transaction> getStatement(UUID customerId) {
        return databaseService.loadTransactions(customerId).stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt))
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByDateRange(UUID customerId, LocalDate from, LocalDate to) {
        return getStatement(customerId).stream()
                .filter(tx -> {
                    LocalDate date = tx.getCreatedAt().toLocalDate();
                    return (date.isEqual(from) || date.isAfter(from)) &&
                            (date.isEqual(to) || date.isBefore(to));
                })
                .collect(Collectors.toList());
    }
}

