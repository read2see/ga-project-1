package com.acme.models;

import java.math.BigDecimal;

public class CardLimits {
    private BigDecimal withdrawLimitPerDay;
    private BigDecimal transferLimitPerDay;
    private BigDecimal transferOwnLimitPerDay;
    private BigDecimal depositLimitPerDay;
    private BigDecimal depositOwnLimitPerDay;

    public CardLimits() { }

    public CardLimits(BigDecimal withdrawLimitPerDay,
                      BigDecimal transferLimitPerDay,
                      BigDecimal transferOwnLimitPerDay,
                      BigDecimal depositLimitPerDay,
                      BigDecimal depositOwnLimitPerDay) {
        this.withdrawLimitPerDay = withdrawLimitPerDay;
        this.transferLimitPerDay = transferLimitPerDay;
        this.transferOwnLimitPerDay = transferOwnLimitPerDay;
        this.depositLimitPerDay = depositLimitPerDay;
        this.depositOwnLimitPerDay = depositOwnLimitPerDay;
    }

    public CardLimits(String withdraw, String transfer, String transferOwn, String deposit, String depositOwn) {
        this(new BigDecimal(withdraw), new BigDecimal(transfer), new BigDecimal(transferOwn),
                new BigDecimal(deposit), new BigDecimal(depositOwn));
    }

    public BigDecimal getWithdrawLimitPerDay() {
        return withdrawLimitPerDay;
    }

    public BigDecimal getTransferLimitPerDay() {
        return transferLimitPerDay;
    }

    public BigDecimal getTransferOwnLimitPerDay() {
        return transferOwnLimitPerDay;
    }

    public BigDecimal getDepositLimitPerDay() {
        return depositLimitPerDay;
    }

    public BigDecimal getDepositOwnLimitPerDay() {
        return depositOwnLimitPerDay;
    }
}

