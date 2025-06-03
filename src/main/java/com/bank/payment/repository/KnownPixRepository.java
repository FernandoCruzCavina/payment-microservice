package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.KnownPixModel;

public interface KnownPixRepository extends JpaRepository<KnownPixModel, Long> {

    Optional<KnownPixModel> existsByIdKeyAndIdAccount(Long idAccount, Long idKey);
}
