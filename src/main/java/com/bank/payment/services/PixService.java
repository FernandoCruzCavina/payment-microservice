package com.bank.payment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bank.payment.models.PixModel;

public interface PixService {

    void save(PixModel pixModel);

    boolean existsByKey(String key);

    List<PixModel> findAll();

    Optional<PixModel> findById(UUID idPix);

    void delete(PixModel pixModel);

}
