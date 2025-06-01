package com.bank.payment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.models.PixModel;
import com.bank.payment.repository.PixRepository;
import com.bank.payment.services.PixService;

@Service
public class PixServiceImp implements PixService {
    @Autowired
    PixRepository pixRepository;

    @Override
    public PixModel save(PixModel pixModel) {
        return pixRepository.save(pixModel);
    }

    @Override
    public Optional<PixModel> findByKey(String key) {
        return pixRepository.findByKey(key);
    }

}
