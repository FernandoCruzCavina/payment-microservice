package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.payment.models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    @Query("SELECT a FROM AccountModel a JOIN PixModel p ON a.idAccount = p.idAccount WHERE p.key = :pixKey")
    Optional<AccountModel> findByPixKey(@Param("pixKey") String pixKey);
}
