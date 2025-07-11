package com.bank.payment.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.payment.dtos.AccountEventDto;
import com.bank.payment.enums.ActionType;
import com.bank.payment.exceptions.ReceiverAccountNotFoundException;
import com.bank.payment.exceptions.SenderAccountNotFoundException;
import com.bank.payment.models.AccountModel;
import com.bank.payment.publishers.PaymentReceiverEventPublisher;
import com.bank.payment.publishers.PaymentSenderEventPublisher;
import com.bank.payment.repository.AccountRepository;
import com.bank.payment.services.AccountService;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    private final PaymentReceiverEventPublisher paymentReceiverEventPublisher;
    private final PaymentSenderEventPublisher paymentSenderEventPublisher;
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    public AccountServiceImpl(PaymentReceiverEventPublisher paymentReceiverEventPublisher, PaymentSenderEventPublisher paymentSenderEventPublisher, AccountRepository accountRepository) {
        this.paymentReceiverEventPublisher = paymentReceiverEventPublisher;
        this.paymentSenderEventPublisher = paymentSenderEventPublisher;
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountModel save(AccountModel accountModel) {
        return accountRepository.save(accountModel);
    }

    @Override
    public AccountModel findSenderAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(SenderAccountNotFoundException::new);
    }

    @Override
    public AccountModel findReceiverAccountByPixKey(String pixKey) {
        return accountRepository.findByPixKey(pixKey)
                .orElseThrow(ReceiverAccountNotFoundException::new);
    }

    @Override
    public void delete(Long idAccount) {
        accountRepository.deleteById(idAccount);
    }

    @Transactional
    @Override
    public AccountModel updateBalanceSender(AccountModel sender) {
        logger.info("Enviando evento para: {}", sender.getIdAccount());

        paymentSenderEventPublisher.publishPaymentSenderEvent(AccountEventDto.fromAccountModel(sender),
                ActionType.PAYMENT);
        return sender;
    }

    @Transactional
    @Override
    public AccountModel updateBalanceReceiver(AccountModel receiver) {
        logger.info("Enviando evento para: {}", receiver.getIdAccount());

        paymentReceiverEventPublisher.publishPaymentReceiverEvent(AccountEventDto.fromAccountModel(receiver),
                ActionType.PAYMENT);
        return receiver;
    }
}
