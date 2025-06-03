package com.bank.payment.services;

import java.util.Optional;

import com.bank.payment.models.AccountModel;

public interface AccountService {

    AccountModel save(AccountModel accountModel);

    Optional<AccountModel> findById(Long idAccount);

    Optional<AccountModel> findByPixKey(String pixKey);

    void delete(Long idAccount);

    AccountModel updateBalance(AccountModel accountModel);
}
