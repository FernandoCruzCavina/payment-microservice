package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PixModel;

/**
 * Repository interface for {@link PixModel} entities.
 * Provides methods for accessing Pix key data in the database.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public interface PixRepository extends JpaRepository<PixModel, Long> {

    /**
     * Finds a Pix key by its value.
     *
     * @param key the Pix key
     * @return an Optional containing the found PixModel, or empty if not found
     */
    Optional<PixModel> findByKey(String key);


}