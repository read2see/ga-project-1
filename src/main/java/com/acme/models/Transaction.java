package com.acme.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    private UUID transactionId;
    private TransactionType type;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private BigDecimal postBalance;
    private LocalDateTime createdAt;
    private String description;
    private String cardLabel;

    public Transaction() { }

    public Transaction(TransactionType type,
                       Account source,
                       BigDecimal amount,
                       Optional<Account> destination,
                       String cardLabel) {
        this.transactionId = UUID.randomUUID();
        this.type = type;
        this.sourceAccountId = source.getAccountId();
        this.destinationAccountId = destination.map(Account::getAccountId).orElse(null);
        this.amount = amount;
        this.postBalance = source.getBalance();
        this.createdAt = LocalDateTime.now();
        this.cardLabel = cardLabel;
        this.description = buildDescription(type, amount, destination);
    }

    private String buildDescription(TransactionType type,
                                    BigDecimal amount,
                                    Optional<Account> destination) {
        Map<TransactionType, String> templates = Map.of(
                TransactionType.WITHDRAW, "Withdrew %s",
                TransactionType.DEPOSIT, "Deposited %s",
                TransactionType.TRANSFER, "Transferred %s to %s",
                TransactionType.OVERDRAFT_FEE, "Overdraft fee of %s"
        );
        String template = templates.getOrDefault(type, type.name());
        if (type == TransactionType.TRANSFER && destination.isPresent()) {
            return template.formatted(amount, destination.get().getAccountNumber());
        }
        return template.formatted(amount);
    }

    @JsonProperty
    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPostBalance() {
        return postBalance;
    }

    public void setPostBalance(BigDecimal postBalance) {
        this.postBalance = postBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }
}

