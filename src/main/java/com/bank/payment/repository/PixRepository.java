package com.bank.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PixModel;

public interface PixRepository extends JpaRepository<PixModel, UUID> {

    boolean existsByKey(String key);
}
