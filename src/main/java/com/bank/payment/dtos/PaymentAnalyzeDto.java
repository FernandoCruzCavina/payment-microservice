package com.bank.payment.dtos;

import java.math.BigDecimal;

/**
 * DTO requested to analyze a payment.
 * 
 * @param senderAccountId the ID of the account making the payment
 * @param receiverPixKey the Pix key of the receiver's account
 * @param paymentDescription a description of the payment
 * @param amountPaid the amount that has been paid
 * @param email the email associated with the payment
 *
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public record PaymentAnalyzeDto(
    Long senderAccountId,
    String receiverPixKey,
    String paymentDescription,
    BigDecimal amountPaid,
    String email
) {}
