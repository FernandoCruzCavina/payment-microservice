package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.payment.models.AccountModel;

/**
 * Repository interface for {@link AccountModel} entities.
 * Provides methods for accessing account data in the database.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    /**
     * Finds an account by its Pix table that associated pix key.
     *
     * @param pixKey the Pix key
     * @return an Optional containing the found AccountModel, or empty if not found
     */
    @Query("SELECT a FROM AccountModel a JOIN PixModel p ON a.idAccount = p.idAccount WHERE p.key = :pixKey")
    Optional<AccountModel> findByPixKey(@Param("pixKey") String pixKey);
}
