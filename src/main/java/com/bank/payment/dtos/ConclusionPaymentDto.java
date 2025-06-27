package com.bank.payment.dtos;

import java.math.BigDecimal;

/**
 * DTO for concluding a payment.
 * This DTO contains the necessary information to finalize a payment transaction.
 * 
 * @param idAccount the ID of the account making the payment
 * @param pixKey the Pix key associated with the payment
 * @param paymentDescription a description of the payment
 * @param amountPaid the amount that has been paid
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public record ConclusionPaymentDto (
    Long idAccount,
    String pixKey,
    String paymentDescription,
    BigDecimal amountPaid
) {}
