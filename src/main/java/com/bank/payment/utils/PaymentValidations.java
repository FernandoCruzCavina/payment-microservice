package com.bank.payment.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import com.bank.payment.exceptions.TransferBalanceToYourselfException;
import com.bank.payment.exceptions.TransferInsuficientBalanceException;
import com.bank.payment.models.AccountModel;

public final class PaymentValidations {

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

    public static boolean wasReceiverAccountCreatedLessThan7DaysAgo(AccountModel receiver) {
        if (receiver.getCreatedAt() == null) {
            throw new IllegalArgumentException("createdAt não pode ser nulo");
        }

        Instant createdAt = Instant.ofEpochMilli(receiver.getCreatedAt());
        Instant sevenDaysAgo = Instant.now().minus(Duration.ofDays(7));

        return createdAt.isAfter(sevenDaysAgo);
    }
}
