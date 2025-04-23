package com.bank.payment.dtos;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PixDto {
    private UUID idPIX;
    private String key;
}
