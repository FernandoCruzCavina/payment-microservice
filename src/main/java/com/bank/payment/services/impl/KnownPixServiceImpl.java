package com.bank.payment.services.impl;

import org.springframework.stereotype.Service;

import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.repository.KnownPixRepository;
import com.bank.payment.services.KnownPixService;

@Service
public class KnownPixServiceImpl implements KnownPixService {

    private final KnownPixRepository knownPixRepository;

    public KnownPixServiceImpl(KnownPixRepository knownPixRepository) {
        this.knownPixRepository = knownPixRepository;
    }

    @Override
    public boolean isTheFirstTransaction(AccountModel senderAccountModel, PixModel receiverPixModel) {
        return knownPixRepository.findByIdAccountAndPixKey(senderAccountModel.getIdAccount(), receiverPixModel.getKey()).isEmpty();
    }

    @Override
    public KnownPixModel saveKnownPixModel(AccountModel senderAccount, PixModel receiverPixModel) {
        return knownPixRepository.save(KnownPixModel.of(senderAccount, receiverPixModel));
    }

}