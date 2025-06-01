package com.bank.payment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.models.AccountModel;
import com.bank.payment.repository.AccountRepository;
import com.bank.payment.services.AccountService;

@Service
public class AccountServiceImp implements AccountService {

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

}
