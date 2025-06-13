package com.bank.payment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.models.KnownPixModel;
import com.bank.payment.repository.KnownPixRepository;
import com.bank.payment.services.KnownPixService;

@Service
public class KnownPixServiceImp implements KnownPixService {

    @Autowired
    KnownPixRepository knownPixRepository;

    @Override
    public Optional<KnownPixModel> existsByIdAccountAndPixKey(Long idAccount, String pixKey) {
        return knownPixRepository.findByIdAccountAndPixKey(idAccount, pixKey);
    }

    @Override
    public KnownPixModel save(KnownPixModel knownPixModel) {
        return knownPixRepository.save(knownPixModel);
    }

}
