package com.bank.payment.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * DTO for payment information.
 * This DTO contains the necessary information about a payment, including its ID,
 * description, and the amount paid.
 * 
 * @param paymentId the ID of the payment
 * @param paymentDescription a description of the payment
 * @param amountPaid the amount that has been paid
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {

    private Long paymentId;

    private String paymentDescription;

    private BigDecimal amountPaid;

    public PaymentDto(String paymentDescription, BigDecimal amountPaid) {
        this.paymentDescription = paymentDescription;
        this.amountPaid = amountPaid;
    }

}
