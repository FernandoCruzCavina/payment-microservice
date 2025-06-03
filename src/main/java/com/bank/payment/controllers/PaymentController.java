package com.bank.payment.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.enums.CurrencyType;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin(origins = "*")
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
        Optional<PaymentModel> paymentModelOptional = paymentService.findById(idPayment);

        if (!paymentModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(paymentModelOptional.get());
        }
    }

    @DeleteMapping("/{idPayment}")
    public ResponseEntity<Object> deletePayment(@PathVariable(value = "idPayment") Long idPayment) {
        Optional<PaymentModel> paymentModelOptional = paymentService.findById(idPayment);

        if (!paymentModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");
        } else {
            paymentService.delete(paymentModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Payment deleted sucessed");
        }
    }

    @PostMapping("/{idAccount}/pix/{pixKey}")
    public ResponseEntity<Object> sendPix(@PathVariable(value = "idAccount") Long idAccount,
            @PathVariable(value = "pixKey") String pixKey, @RequestBody @Validated PaymentDto paymentDto) {
        Optional<AccountModel> accountSenderModelOptional = accountService.findById(idAccount);
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(pixKey);
        Optional<PixModel> pixModelOptional = pixService.findByKey(pixKey);
        // Optional<KnownPixModel> knownPixModelExists =
        // knownPixService.existsByIdKeyAndIdAccount(idAccount,
        // pixModelOptional.get().getIdPix());

        if (!accountSenderModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account sender not found!");
        }

        if (!pixModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pix not found!");
        }

        if (!accountReceiveModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account receiver not found!");
        }

        // if (knownPixModelExists.isPresent()) {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body(knownPixModelExists.get());
        // }

        var paymentModel = new PaymentModel();
        // var knownPixModel = new KnownPixModel();

        // long sevenDaysAgoEpoch =
        // java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();

        // if (accountReceiveModelOptional.get().getCreatedAt() >= sevenDaysAgoEpoch) {
        // return ResponseEntity.status(HttpStatus.LOCKED)
        // .body("The account that will receive the Pix was created less than 7 days
        // ago.");
        // }

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setCurrencyType(CurrencyType.BRL);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(accountReceiveModelOptional.get());
        paymentModel.setSenderAccount(accountSenderModelOptional.get());
        paymentModel.setPaymentType(PaymentType.PIX);

        accountSenderModelOptional.get()
                .setBalance(accountSenderModelOptional.get().getBalance().subtract(paymentModel.getAmountPaid()));

        accountReceiveModelOptional.get()
                .setBalance(accountReceiveModelOptional.get().getBalance().add(paymentModel.getAmountPaid()));

        // knownPixModel.setIdAccount(accountSenderModelOptional.get().getIdAccount());
        // knownPixModel.setIdKey(pixModelOptional.get().getIdPix());

        System.out.println("Sender: " + accountSenderModelOptional.get().getIdAccount());
        System.out.println("Receiver: " + accountReceiveModelOptional.get().getIdAccount());

        paymentService.save(paymentModel);
        accountService.updateBalance(accountSenderModelOptional.get());
        accountService.updateBalance(accountReceiveModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body(paymentModel);
    }

}
