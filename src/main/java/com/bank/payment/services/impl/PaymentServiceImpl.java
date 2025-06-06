package com.bank.payment.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.models.PaymentModel;
import com.bank.payment.publishers.PaymentEventPublisher;
import com.bank.payment.repository.PaymentRepository;
import com.bank.payment.services.PaymentService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

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
        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());

        return paymentModel;
    }

}
