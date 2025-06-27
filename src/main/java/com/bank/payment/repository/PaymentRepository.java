package com.bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PaymentModel;

/**
 * Repository interface for {@link PaymentModel} entities.
 * Provides methods for accessing payment data in the database.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {

}
