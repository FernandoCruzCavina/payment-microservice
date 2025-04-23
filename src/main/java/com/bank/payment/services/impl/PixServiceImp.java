package com.bank.payment.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public void save(PixModel pixModel) {
        pixRepository.save(pixModel);
    }

    @Override
    public boolean existsByKey(String key) {
        return pixRepository.existsByKey(key);
    }

    @Override
    public List<PixModel> findAll() {
        return pixRepository.findAll();
    }

    @Override
    public Optional<PixModel> findById(UUID idPix) {
        return pixRepository.findById(idPix);
    }

    @Override
    public void delete(PixModel pixModel) {
        pixRepository.delete(pixModel);

    }
}
