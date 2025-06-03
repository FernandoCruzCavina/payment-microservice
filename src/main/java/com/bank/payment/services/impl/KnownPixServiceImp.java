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
    public Optional<KnownPixModel> existsByIdKeyAndIdAccount(Long idAccount, Long idKey) {
        return knownPixRepository.existsByIdKeyAndIdAccount(idAccount, idKey);
    }

    @Override
    public KnownPixModel save(KnownPixModel knownPixModel) {
        return knownPixRepository.save(knownPixModel);
    }

}
