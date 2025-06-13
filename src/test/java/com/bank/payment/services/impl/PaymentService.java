package com.bank.payment.services.impl;

import java.util.List;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.models.PaymentModel;

public interface PaymentService {

    List<PaymentModel> findAll();

    PaymentModel findById(Long idPayment);

    String delete(Long idPayment);

    PaymentModel save(PaymentModel paymentModel);

    PaymentModel savePayment(PaymentModel paymentModel);

    String analyzePayment(Long idAccount, String pixKey, PaymentAnalyzeDto paymentAnalyzeDto);

    void sendPix(ConclusionPaymentDto paymentDto);

    String directPayment(Long idAccount, String pixKey, PaymentDto paymentDto);
}
