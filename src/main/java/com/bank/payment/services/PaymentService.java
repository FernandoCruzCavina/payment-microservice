package com.bank.payment.services;

import javax.security.auth.login.AccountNotFoundException;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.repository.PaymentRepository;

/**
 * Interface for Payment Service that defines methods for handling payment operations.
 * It includes methods for finding, deleting, saving payments, analyzing payments,
 * sending Pix transactions, and performing direct payments.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see PaymentRepository
 * @since 1.0.0
 */
public interface PaymentService {

    PaymentModel findById(Long idPayment);

    String delete(Long idPayment);

    /**
     * Saves a payment model to the database and publishes an event for the payment save and other microservices.
     * <p>
     * For example, it can be used to save a payment after analyzing it or processing a direct payment.
     * @param paymentModel the payment model to be saved
     * @return {@link PaymentModel} - the saved payment model
     */
    PaymentModel savePayment(PaymentModel paymentModel);

    /**
     * Analyzes both accounts (sender and receiver) and the Pix key to ensure that the payment can be processed.
     * <p>
     * This method checks if the sender has sufficient balance, if the receiver account exists, and if the Pix key is valid.
     * 
     * @param idAccount the ID of the account making the payment
     * @param pixKey the Pix key of the receiver
     * @param paymentAnalyzeDto the DTO containing payment analysis details
     * @return a String message asking the user to confirm the payment or an error message if the analysis fails
     */
    String reviewPaymentBeforeProcessing(Long idAccount, String pixKey, PaymentAnalyzeDto paymentAnalyzeDto);

    /**
     * Sends a Pix transactions to the receiver's account after the payment has been analyzed and confirmed.
     * 
     * @param paymentDto the DTO containing, for example, the payment amount, sender and receiver details
     */
    void sendPix(ConclusionPaymentDto paymentDto);

    /**
     * Processes a direct payment by performing the necessary checks and sending the Pix transaction.
     * 
     * @param idAccount the ID of the account making the payment
     * @param pixKey the Pix key of the receiver
     * @param paymentDto the DTO containing payment details
     * @return a String message indicating the result of the direct payment operation
     */
    String directPayment(Long idAccount, String pixKey, PaymentDto paymentDto);
}
