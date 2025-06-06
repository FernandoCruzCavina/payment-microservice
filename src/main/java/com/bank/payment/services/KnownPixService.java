package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.KnownPixModel;

public interface KnownPixService {
    Optional<KnownPixModel> existsByIdAccountAndPixKey(Long idAccount, String pixKey);

    KnownPixModel save(KnownPixModel knownPixModel);
}
