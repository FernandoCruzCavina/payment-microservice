package com.bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PaymentModel;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

}
