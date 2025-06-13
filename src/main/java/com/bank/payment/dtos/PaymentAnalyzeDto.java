package com.bank.payment.dtos;

import java.math.BigDecimal;

public record PaymentAnalyzeDto(
    String paymentDescription,
    BigDecimal amountPaid,
    String email
) {}
