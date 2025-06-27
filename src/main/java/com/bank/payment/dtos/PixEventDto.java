package com.bank.payment.dtos;

import org.springframework.beans.BeanUtils;

import com.bank.payment.models.PixModel;

import lombok.Data;
/**
 * Represents a Pix event to be transferred in the account microservice.
 * This DTO is used to transfer Pix event data such as key, creation time, last update time,
 * action type, and associated account ID.
 */
@Data
public class PixEventDto {
    private Long idPix;

    private String key;

    private Long createdAt;

    private Long lastUpdatedAt;

    private String actionType;

    private Long idAccount;

    public PixModel toPixModel() {
        var pixModel = new PixModel();

        BeanUtils.copyProperties(this, pixModel);
        return pixModel;
    }

}
