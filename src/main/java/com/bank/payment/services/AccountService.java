package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.AccountModel;

/**
 * Interface for AccountService that defines methods for handling account operations.
 * It includes methods for saving, finding, deleting accounts, and updating account balances.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @see AccountModel
 * @since 1.0.0
 */
public interface AccountService {

    /**
     * Saves an AccountModel to the database.
     * 
     * @param accountModel the AccountModel to be saved
     * @return the saved AccountModel
     */
    AccountModel save(AccountModel accountModel);

    /**
     * Finds an AccountModel by its ID.
     * 
     * @param idAccount the ID of the account to search for
     * @return an Optional containing the AccountModel if found, or empty if not found
     */
    Optional<AccountModel> findById(Long idAccount);

    /**
     * Finds an AccountModel by its Pix key.
     * 
     * @param pixKey the Pix key to search for
     * @return an Optional containing the AccountModel if found, or empty if not found
     */
    Optional<AccountModel> findByPixKey(String pixKey);

    /**
     * Deletes an AccountModel by its ID.
     * 
     * @param idAccount the ID of the account to be deleted
     */
    void delete(Long idAccount);

    /**
     * Updates the balance of the account receiving a payment.
     * 
     * @param accountModel the AccountModel with updated balance information
     * @return the updated AccountModel
     */
    AccountModel updateBalanceReceive(AccountModel accountModel);

    /**
     * Updates the balance of the account sending a payment.
     * 
     * @param accountModel the AccountModel with updated balance information
     * @return the updated AccountModel
     */
    AccountModel updateBalanceSender(AccountModel accountModel);
}
