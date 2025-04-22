package com.bank.payment.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bank.payment.Enums.CurrencyType;
import com.bank.payment.Enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_PAYMENTS")
public class PaymentModel {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPayment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(length = 150)
    private String paymentDescription;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Column(nullable = false)
    private LocalDateTime paymentRequestDate;

    @Column(nullable = false)
    private LocalDateTime paymentCompletionDate;

}
