package com.bank.payment.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {

    private UUID paymentId;
    private String paymentDescription;
    private BigDecimal amountPaid;

}
