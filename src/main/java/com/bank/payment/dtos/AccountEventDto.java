package com.bank.payment.dtos;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.bank.payment.models.AccountModel;

import lombok.Data;

/**
 * DTO for update account model information in account microservice.
 * 
 * @param idAccount the ID of the account
 * @param balance the current balance of the account
 * @param createdAt the timestamp when the account was created
 * @param lastUpdatedAt the timestamp when the account was last updated
 * @param imageUrl the URL of the account's image
 * @param actionType the type of action (CREATE, UPDATE, etc.)
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Data
public class AccountEventDto {

    private Long idAccount;
    private BigDecimal balance;
    private Long createdAt;
    private Long lastUpdatedAt;
    private String imageUrl;
    private String actionType;

    public AccountModel toAccountModel() {
        var accountModel = new AccountModel();

        BeanUtils.copyProperties(this, accountModel);
        return accountModel;
    }
}
