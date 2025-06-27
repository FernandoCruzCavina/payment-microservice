package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.PixModel;
import com.bank.payment.repository.PixRepository;

/**
 * Interface for Pix Service that defines methods for handling Pix operations.
 * It includes methods for saving a PixModel and finding a PixModel by its key.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see PixRepository
 * @since 1.0.0
 */
public interface PixService {

    /**
     * Saves a PixModel to the database.
     * 
     * @param pixModel the PixModel to be saved
     * @return the saved PixModel
     */
    PixModel save(PixModel pixModel);

    /**
     * Finds a PixModel by its key.
     * 
     * @param key the Pix key to search for
     * @return an Optional containing the PixModel if found, or empty if not found
     */
    Optional<PixModel> findByKey(String key);

}
