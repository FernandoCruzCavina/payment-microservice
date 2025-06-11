package com.bank.payment.dtos;

import java.math.BigDecimal;

public record ConclusionPaymentDto (
    Long idAccount,
    String pixKey,
    String paymentDescription,
    BigDecimal amountPaid
) {}
