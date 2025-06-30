package com.bank.payment.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.bank.payment.dtos.AccountEventDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an account in the payment microservice.
 * This model is used to store account information such as balance, creation date,
 * last updated date, and associated payments.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see PaymentModel
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_ACCOUNTS")
public class AccountModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long idAccount;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long lastUpdatedAt;

    @Column(nullable = true)
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY)
    private Set<PaymentModel> sendPayment;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY)
    private Set<PaymentModel> receivePayment;

    public AccountEventDto toAccountEventDto() {
        var accountEventDto = new AccountEventDto();

        BeanUtils.copyProperties(this, accountEventDto);

        return accountEventDto;
    }
}
