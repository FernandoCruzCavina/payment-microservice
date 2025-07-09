package com.bank.payment.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.repository.KnownPixRepository;
import com.bank.payment.services.KnownPixService;

@Service
public class KnownPixServiceImpl implements KnownPixService {

    private final KnownPixRepository knownPixRepository;
    private static final Logger logger = LoggerFactory.getLogger(KnownPixServiceImpl.class);

    public KnownPixServiceImpl(KnownPixRepository knownPixRepository) {
        this.knownPixRepository = knownPixRepository;
    }

    @Override
    public Optional<KnownPixModel> existsByIdAccountAndPixKey(Long idAccount, String pixKey) {
        return knownPixRepository.findByIdAccountAndPixKey(idAccount, pixKey);
    }

    @Override
    public boolean isTheFirstTransaction(AccountModel senderAccountModel, PixModel receiverPixModel) {
        Optional<KnownPixModel> knownPixModelExists = existsByIdAccountAndPixKey(senderAccountModel.getIdAccount(), receiverPixModel.getKey());
        
        return knownPixModelExists.isEmpty();
    }

    @Override
    public KnownPixModel saveKnownPixModel(AccountModel senderAccount, PixModel receiverPixModel) {
        var knownPixModel = new KnownPixModel();

        knownPixModel.setIdAccount(senderAccount.getIdAccount());
        knownPixModel.setPixKey(receiverPixModel.getKey());

        return knownPixRepository.save(knownPixModel);
    }

    @Override
    public boolean wasReceiverAccountCreatedLessThan7DaysAgo(AccountModel receiverAccountModel){
        long sevenDaysAgoEpoch = java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();

        logger.info("Seven days ago epoch: {}", sevenDaysAgoEpoch);

        if (receiverAccountModel.getCreatedAt() >= sevenDaysAgoEpoch) return true;
        
        return false;
    }

}