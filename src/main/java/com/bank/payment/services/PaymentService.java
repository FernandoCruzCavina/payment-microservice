package com.bank.payment.services;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.exceptions.PaymentNotFoundException;
import com.bank.payment.exceptions.ReceiverAccountNotFoundException;
import com.bank.payment.exceptions.SenderAccountNotFoundException;
import com.bank.payment.exceptions.TransferInsuficientBalanceException;
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

    /**
     * Finds a payment by its ID.
     * 
     * @param idPayment the ID of the payment to find
     * @return the PaymentModel associated with the given ID
     * @throws PaymentNotFoundException if the payment with the given ID is not found
     */
    PaymentModel findById(Long idPayment);

    /**
     * Deletes a payment by its ID.
     * 
     * @param idPayment the ID of the payment to delete
     * @return a String message indicating the result of the deletion operation
     * @throws PaymentNotFoundException if the payment with the given ID is not found
     */
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
     * @param paymentAnalyzeDto the DTO containing payment analysis details
     * @return a String message asking the user to confirm the payment or an error message if the analysis fails
     * @throws ReceiverAccountNotFoundException if the receiver account is not found
     * @throws SenderAccountNotFoundException if the sender account is not found
     * @throws PixKeyNotFoundException if the Pix key of the receiver is not found
     * @throws InsufficientBalanceException if the sender account does not have sufficient balance for the payment
     * @throws TransferInsuficientBalanceException if the sender account does not have sufficient balance for the transfer
     */
    String reviewPaymentBeforeProcessing(PaymentAnalyzeDto paymentAnalyzeDto);

    /**
     * Sends a Pix transactions to the receiver's account after the payment has been analyzed and confirmed.
     * 
     * @param paymentDto the DTO containing, for example, the payment amount, sender and receiver details
     * @return a String message indicating the result of the direct payment operation
     */
    String sendPix(ConclusionPaymentDto paymentDto);
}
