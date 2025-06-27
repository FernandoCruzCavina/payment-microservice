package com.bank.payment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.enums.ActionType;
import com.bank.payment.models.AccountModel;
import com.bank.payment.publishers.PaymentReceiverEventPublisher;
import com.bank.payment.publishers.PaymentSenderEventPublisher;
import com.bank.payment.repository.AccountRepository;
import com.bank.payment.services.AccountService;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImp implements AccountService {

    @Autowired
    PaymentReceiverEventPublisher paymentReceiverEventPublisher;

    @Autowired
    PaymentSenderEventPublisher paymentSenderEventPublisher;

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
    public AccountModel updateBalanceSender(AccountModel accountModel) {

        System.out.println("Enviando evento para: " + accountModel.getIdAccount());

        paymentSenderEventPublisher.publishPaymentSenderEvent(accountModel.toAccountEventDto(),
                ActionType.PAYMENT);
        return accountModel;
    }

    @Transactional
    @Override
    public AccountModel updateBalanceReceive(AccountModel accountModel) {

        System.out.println("Enviando evento para: " + accountModel.getIdAccount());

        paymentReceiverEventPublisher.publishPaymentReceiverEvent(accountModel.toAccountEventDto(),
                ActionType.PAYMENT);
        return accountModel;
    }

}
