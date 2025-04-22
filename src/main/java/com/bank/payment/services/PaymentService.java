package com.bank.payment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bank.payment.models.PaymentModel;

public interface PaymentService {

    List<PaymentModel> findAll();

    Optional<PaymentModel> findById(UUID idPayment);

    void delete(PaymentModel paymentModel);

}
