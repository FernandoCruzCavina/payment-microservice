package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.KnownPixModel;

/**
 * Repository interface for {@link KnownPixModel} entities.
 * Provides methods for accessing known Pix keys associated with accounts.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public interface KnownPixRepository extends JpaRepository<KnownPixModel, Long> {

    /**
     * Finds a known Pix key by account ID and Pix key.
     *
     * @param idAccount the account ID
     * @param pixKey the Pix key
     * @return an Optional containing the found KnownPixModel, or empty if not found
     */
    Optional<KnownPixModel> findByIdAccountAndPixKey(Long idAccount, String pixKey);
}
