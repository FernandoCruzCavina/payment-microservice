package com.bank.payment.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bank.payment.models.AccountModel;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.publishers.PaymentEventPublisher;
import com.bank.payment.repository.PaymentRepository;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

    @Autowired
    AccountService accountService;

    @Autowired
    KnownPixService knownPixService;

    @Autowired
    PixService pixService;

    @Override
    public List<PaymentModel> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<PaymentModel> findById(Long idPayment) {
        return paymentRepository.findById(idPayment);
    }

    @Override
    public void delete(PaymentModel paymentModel) {
        paymentRepository.delete(paymentModel);
    }

    @Override
    public PaymentModel save(PaymentModel paymentModel) {
        return paymentRepository.save(paymentModel);
    }

    @Transactional
    @Override
    public PaymentModel savePayment(PaymentModel paymentModel) {
        save(paymentModel);

        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());
        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());

        return paymentModel;
    }

    @Override
    public ResponseEntity<Object> validatePresence(Long idAccount, String pixKey) {
        Optional<AccountModel> accountSenderModelOptional = accountService.findById(idAccount);
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(pixKey);
        Optional<PixModel> pixModelOptional = pixService.findByKey(pixKey);

        if (!accountSenderModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account sender not found!");
        }

        if (!pixModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pix not found!");
        }

        if (!accountReceiveModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account receiver not found!");
        }

        return null;
    }

}
