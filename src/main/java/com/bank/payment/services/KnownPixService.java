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
     * Checks if a known Pix exists by account ID and Pix key.
     * 
     * @param idAccount the ID of the account associated with the Pix key
     * @param pixKey the Pix key to check
     * @return an Optional containing the KnownPixModel if it exists, or empty if not found
     */
    Optional<KnownPixModel> existsByIdAccountAndPixKey(Long idAccount, String pixKey);

    /**
     * Saves the first transactions between SenderAccount and ReceiverAccount.
     * 
     * @param senderAccount
     * @param receiverPixModel
     * @return the saved KnownPixModel
     */
    KnownPixModel saveKnownPixModel(AccountModel senderAccount, PixModel receiverPixModel);

    boolean isTheFirstTransaction(AccountModel senderAccountModel, PixModel receiverPixModel);

    boolean wasReceiverAccountCreatedLessThan7DaysAgo(AccountModel receiverAccountModel);
}
