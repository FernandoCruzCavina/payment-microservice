package com.bank.payment.dtos;

import java.math.BigDecimal;

public record PaymentRequestDto(
    long idAccount,
    String pixKey,
    String paymentDescription,
    BigDecimal amountPaid
) {}
