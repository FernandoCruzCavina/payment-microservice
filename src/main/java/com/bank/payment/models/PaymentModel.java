package com.bank.payment.models;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.bank.payment.dtos.PaymentEventDto;
import com.bank.payment.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a payment in the payment microservice.
 * This model is used to store payment information such as amount, sender, receiver,
 * and payment status.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see AccountModel
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_PAYMENTS")
public class PaymentModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long idPayment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(length = 150)
    private String paymentDescription;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private Long paymentRequestDate;

    @Column(nullable = false)
    private Long paymentCompletionDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AccountModel senderAccount;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AccountModel receiverAccount;

    public PaymentEventDto convertToPaymentEventDto() {
        var paymentEventDto = new PaymentEventDto();

        BeanUtils.copyProperties(this, paymentEventDto);
        paymentEventDto.setReceiverAccount(this.getReceiverAccount().getIdAccount());
        paymentEventDto.setSenderAccount(this.getSenderAccount().getIdAccount());
        paymentEventDto.setPaymentDescription(this.paymentDescription);
        return paymentEventDto;
    }
}
