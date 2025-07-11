package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
import com.bank.payment.models.PixModel;

/**
 * Interface for KnownPixService that defines methods for handling known Pix operations.
 * It includes methods for checking if a known Pix exists by account ID and Pix key,
 * and for saving a KnownPixModel.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see KnownPixModel
 * @since 1.0.0
 */
public interface KnownPixService {
    /**
     * Saves the first transactions between SenderAccount and ReceiverAccount.
     * 
     * @param senderAccount the account of the sender
     * @param receiverPixModel the PixModel of the receiver
     * @return the saved KnownPixModel
     */
    KnownPixModel saveKnownPixModel(AccountModel senderAccount, PixModel receiverPixModel);

    /**
     * Checks if already exists a KnownPixModel, in other words, if it is the first transaction for the given sender account ID and receiver Pix key.
     * 
     * @param senderAccountId the ID of the sender's account
     * @param receiverPixKey the Pix key of the receiver
     * @return an true if the KnownPixModel exists, false otherwise
     */
    boolean isTheFirstTransaction(AccountModel senderAccountModel, PixModel receiverPixModel);

}
