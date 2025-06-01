package com.bank.payment.services;

import java.util.List;
import java.util.Optional;

import com.bank.payment.models.PaymentModel;

public interface PaymentService {

    List<PaymentModel> findAll();

    Optional<PaymentModel> findById(Long idPayment);

    void delete(PaymentModel paymentModel);

    PaymentModel save(PaymentModel paymentModel);

}
