package com.bank.payment.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Represents a known Pix key associated with an account in the payment microservice.
 * This DTO is used to transfer Pix keys that are recognized and associated with specific accounts.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnownPixDto {
    private String pixKey;
    private String idAccount;
}
