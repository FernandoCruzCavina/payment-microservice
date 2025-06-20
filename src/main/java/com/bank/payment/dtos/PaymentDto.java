package com.bank.payment.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

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
