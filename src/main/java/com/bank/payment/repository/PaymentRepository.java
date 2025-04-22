package com.bank.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PaymentModel;

public interface PaymentRepository extends JpaRepository<PaymentModel, UUID> {

}
