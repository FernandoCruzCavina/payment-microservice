package com.bank.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.payment.models.PixModel;

public interface PixRepository extends JpaRepository<PixModel, Long> {
    Optional<PixModel> findByKey(String key);

}
