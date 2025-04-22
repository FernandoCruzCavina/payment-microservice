package com.bank.payment.controllers;

import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.models.PaymentModel;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<PaymentModel>> getAllPayments() {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findAll());
    }

    @GetMapping("/{idPayment}")
    public ResponseEntity<Object> getOnePayment(@PathVariable(value = "idPayment") UUID idPayment) {
        Optional<PaymentModel> paymentModelOptional = paymentService.findById(idPayment);

        if (!paymentModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(paymentModelOptional.get());
        }
    }

    @DeleteMapping("/{idPayment}")
    public ResponseEntity<Object> deletePayment(@PathVariable(value = "idPayment") UUID idPayment) {
        Optional<PaymentModel> paymentModelOptional = paymentService.findById(idPayment);

        if (!paymentModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        } else {
            paymentService.delete(paymentModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Payment deleted sucessed");
        }
    }

}
