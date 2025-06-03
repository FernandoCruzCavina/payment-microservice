package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.KnownPixModel;

public interface KnownPixService {
    Optional<KnownPixModel> existsByIdKeyAndIdAccount(Long idAccount, Long idKey);

    KnownPixModel save(KnownPixModel knownPixModel);
}
