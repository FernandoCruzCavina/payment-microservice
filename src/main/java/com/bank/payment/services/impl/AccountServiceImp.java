package com.bank.payment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.enums.ActionType;
import com.bank.payment.models.AccountModel;
import com.bank.payment.publishers.AccountEventPublisher;
import com.bank.payment.repository.AccountRepository;
import com.bank.payment.services.AccountService;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImp implements AccountService {

    @Autowired
    AccountEventPublisher accountEventPublisher;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public AccountModel save(AccountModel accountModel) {
        return accountRepository.save(accountModel);
    }

    @Override
    public Optional<AccountModel> findById(Long idAccount) {
        return accountRepository.findById(idAccount);
    }

    @Override
    public Optional<AccountModel> findByPixKey(String pixKey) {
        return accountRepository.findByPixKey(pixKey);
    }

    @Override
    public void delete(Long idAccount) {
        accountRepository.deleteById(idAccount);
    }

    @Transactional
    @Override
    public AccountModel updateBalance(AccountModel accountModel) {

        System.out.println("Enviando evento para: " + accountModel.getIdAccount());

        accountEventPublisher.publishAccountEvent(accountModel.convertToAccountEventDto(), ActionType.PAYMENT);
        return accountModel;
    }

}
