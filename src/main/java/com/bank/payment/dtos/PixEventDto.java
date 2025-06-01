package com.bank.payment.dtos;

import org.springframework.beans.BeanUtils;

import com.bank.payment.models.PixModel;

import lombok.Data;

@Data
public class PixEventDto {
    private Long idPix;

    private String key;

    private Long createdAt;

    private Long lastUpdatedAt;

    private String actionType;

    private Long idAccount;

    public PixModel convertToPixModel() {
        var pixModel = new PixModel();

        BeanUtils.copyProperties(this, pixModel);
        return pixModel;
    }

}
