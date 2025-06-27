package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.KnownPixModel;

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
     * Saves a KnownPixModel to the database.
     * 
     * @param knownPixModel the KnownPixModel to be saved
     * @return the saved KnownPixModel
     */
    KnownPixModel save(KnownPixModel knownPixModel);
}
