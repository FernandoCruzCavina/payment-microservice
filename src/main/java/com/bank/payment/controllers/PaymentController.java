package com.bank.payment.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

@RestController
@RequestMapping()
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountService accountService;

    @Autowired
    KnownPixService knownPixService;

    @Autowired
    PixService pixService;

    @GetMapping()
    public ResponseEntity<List<PaymentModel>> getAllPayments() {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.findAll());
    }

    @GetMapping("/{idPayment}")
    public ResponseEntity<Object> getOnePayment(@PathVariable(value = "idPayment") Long idPayment) {
        PaymentModel paymentModel = paymentService.findById(idPayment);

        return ResponseEntity.status(HttpStatus.OK).body(paymentModel);
    }

    @DeleteMapping("/{idPayment}")
    public ResponseEntity<Object> deletePayment(@PathVariable(value = "idPayment") Long idPayment) {
        String response = paymentService.delete(idPayment); 

        return ResponseEntity.status(HttpStatus.OK).body(response);
        
    }

    @PostMapping("/{idAccount}/pix/{pixKey}")
    public ResponseEntity<Object> analyzePayment(@PathVariable(value = "idAccount") Long idAccount,
            @PathVariable(value = "pixKey") String pixKey, @RequestBody PaymentAnalyzeDto paymentDto) {
        String response = paymentService.analyzePayment(idAccount, pixKey, paymentDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{idAccount}/pix/{pixKey}/direct")
    public ResponseEntity<Object> directPayment(@PathVariable(value = "idAccount") Long idAccount,
            @PathVariable(value = "pixKey") String pixKey, @RequestBody @Validated PaymentDto paymentDto) {
        String response = paymentService.directPayment(idAccount, pixKey, paymentDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
