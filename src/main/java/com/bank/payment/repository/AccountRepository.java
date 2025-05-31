package com.bank.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {

}
