package com.bank.payment.dtos;

import java.math.BigDecimal;

import lombok.Data;

/**
 * DTO for payment events.
 * This DTO contains the necessary information about a payment event, including
 * the payment ID, description, amount paid, request and completion dates,
 * and the sender and receiver accounts.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Data
public class PaymentEventDto {
    private Long idPayment;
    private String paymentDescription;
    private BigDecimal amountPaid;
    private Long paymentRequestDate;
    private Long paymentCompletionDate;

    private Long senderAccount;
    private Long receiverAccount;
}
