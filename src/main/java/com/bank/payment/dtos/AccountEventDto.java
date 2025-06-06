package com.bank.payment.dtos;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.bank.payment.models.AccountModel;

import lombok.Data;

@Data
public class AccountEventDto {

    private Long idAccount;
    private BigDecimal balance;
    private Long createdAt;
    private Long lastUpdatedAt;
    private String imageUrl;
    private String actionType;

    public AccountModel convertToAccountModel() {
        var accountModel = new AccountModel();

        BeanUtils.copyProperties(this, accountModel);
        return accountModel;
    }
}
