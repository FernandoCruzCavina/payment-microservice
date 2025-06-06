package com.bank.payment.dtos;

import java.math.BigDecimal;

public record PaymentConfirmationDto(
        boolean isConfirm,
        long idAccount,
        String pixKey,
        String paymentDescription,
        BigDecimal amountPaid) {
}
