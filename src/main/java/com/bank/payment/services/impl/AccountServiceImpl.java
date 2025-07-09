package com.bank.payment.services.impl;

import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.payment.enums.ActionType;
import com.bank.payment.exceptions.AccountReceiverNotFoundException;
import com.bank.payment.exceptions.AccountSenderNotFoundException;
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
    public AccountModel findByIdOrThrow(Long idAccount, Supplier<? extends RuntimeException> exceptionSupplier) {
        return accountRepository.findById(idAccount)
                .orElseThrow(exceptionSupplier);
    }

    @Override
    public AccountModel findByPixKeyOrThrow(String pixKey, Supplier<? extends RuntimeException> exceptionSupplier) {
        return accountRepository.findByPixKey(pixKey)
                .orElseThrow(exceptionSupplier);
    }

    @Override
    public void delete(Long idAccount) {
        accountRepository.deleteById(idAccount);
    }

    @Transactional
    @Override
    public AccountModel updateBalanceSender(AccountModel accountModel) {
        logger.info("Enviando evento para: {}", accountModel.getIdAccount());

        paymentSenderEventPublisher.publishPaymentSenderEvent(accountModel.toAccountEventDto(),
                ActionType.PAYMENT);
        return accountModel;
    }

    @Transactional
    @Override
    public AccountModel updateBalanceReceive(AccountModel accountModel) {
        logger.info("Enviando evento para: {}", accountModel.getIdAccount());

        paymentReceiverEventPublisher.publishPaymentReceiverEvent(accountModel.toAccountEventDto(),
                ActionType.PAYMENT);
        return accountModel;
    }
}
