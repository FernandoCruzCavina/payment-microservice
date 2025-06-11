package com.bank.payment.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.models.PaymentModel;

public interface PaymentService {

    List<PaymentModel> findAll();

    Optional<PaymentModel> findById(Long idPayment);

    void delete(PaymentModel paymentModel);

    PaymentModel save(PaymentModel paymentModel);

    PaymentModel savePayment(PaymentModel paymentModel);

    String analyzePayment(Long idAccount, String pixKey, String email);

    void sendPix(ConclusionPaymentDto paymentDto);
}
