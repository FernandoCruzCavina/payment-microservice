package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.PixModel;

public interface PixService {

    PixModel save(PixModel pixModel);

    Optional<PixModel> findByKey(String key);

}
