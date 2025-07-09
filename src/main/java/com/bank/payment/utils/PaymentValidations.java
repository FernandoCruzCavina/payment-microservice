package com.bank.payment.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.bank.payment.exceptions.TransferBalanceToYourselfException;
import com.bank.payment.exceptions.TransferInsuficientBalanceException;
import com.bank.payment.models.AccountModel;

public final class PaymentValidations {

    public PaymentValidations() {}

    public static void validateNotTransferringToSelf(AccountModel sender, AccountModel receiver) {
        if (sender.getIdAccount().equals(receiver.getIdAccount())) {
            throw new TransferBalanceToYourselfException();
        }
    }

    public static void validateSufficientBalance(AccountModel sender, BigDecimal amountPaid) {
        if (sender.getBalance().compareTo(amountPaid) <= 0) {
            throw new TransferInsuficientBalanceException();
        }
    }

    public static boolean isAccountYoungerThanSevenDays(AccountModel account) {
        long sevenDaysAgoEpoch = Instant.now().minus(Duration.ofDays(7)).getEpochSecond();
        return account.getCreatedAt() >= sevenDaysAgoEpoch;
    }
}
