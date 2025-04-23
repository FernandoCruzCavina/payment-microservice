package com.bank.payment.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dtos.PixDto;
import com.bank.payment.enums.PixKeyType;
import com.bank.payment.models.PixModel;
import com.bank.payment.services.PixService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("payments/pix")
public class PixController {
    @Autowired
    PixService pixService;

    @GetMapping()
    public ResponseEntity<List<PixModel>> getAllPixs() {
        return ResponseEntity.status(HttpStatus.OK).body(pixService.findAll());

    }

    @GetMapping("/{idPix}")
    public ResponseEntity<Object> getById(@PathVariable("idPix") UUID idPix) {
        Optional<PixModel> pixModelOptional = pixService.findById(idPix);

        if (!pixModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pix key not found.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(pixModelOptional.get());
        }

    }

    @DeleteMapping("/{idPix}")
    public ResponseEntity<Object> deletePix(@PathVariable("idPix") UUID idPix) {
        Optional<PixModel> pixModelOptional = pixService.findById(idPix);

        if (!pixModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pix key not found.");
        } else {
            pixService.delete(pixModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(pixModelOptional.get());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerPix(@RequestBody PixDto pixDto) {
        if (pixService.existsByKey(pixDto.getKey())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Key is already taken!");
        }

        var pixModel = new PixModel();

        BeanUtils.copyProperties(pixDto, pixModel);

        pixModel.setPixKeyType(PixKeyType.TELEFONE);
        pixService.save(pixModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(pixModel);
    }

}