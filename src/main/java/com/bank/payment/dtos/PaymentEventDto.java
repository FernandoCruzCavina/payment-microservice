package com.bank.payment.dtos;

import java.math.BigDecimal;

import lombok.Data;

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
