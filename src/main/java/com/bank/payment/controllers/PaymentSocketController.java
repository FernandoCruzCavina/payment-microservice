package com.bank.payment.controllers;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bank.payment.dtos.PaymentConfirmationDto;
import com.bank.payment.dtos.PaymentRequestDto;
import com.bank.payment.enums.CurrencyType;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.models.AccountModel;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

@Controller
public class PaymentSocketController{

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private KnownPixService knownPixService;

    @Autowired
    private PixService pixService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final String confirmQueue = "/queue/confirm";
    private final String statusQueue = "/queue/status";

    @MessageMapping("/request")
    public void analyzePaymentRequest(PaymentRequestDto paymentDto, @Header("username")String username){
        
        Optional<AccountModel> accountSenderModelOptional = accountService.findById(paymentDto.idAccount());
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(paymentDto.pixKey());
        Optional<PixModel> pixModelOptional = pixService.findByKey(paymentDto.pixKey());
    
        if (!accountSenderModelOptional.isPresent()) {
            messagingTemplate.convertAndSendToUser(username, confirmQueue, "Account sender not found!");
            return;
        }

        if (!pixModelOptional.isPresent()) {
            messagingTemplate.convertAndSendToUser(username, confirmQueue, "Pix not found!");
            return;
        }
        
        if (!accountReceiveModelOptional.isPresent()) {
            messagingTemplate.convertAndSendToUser(username, confirmQueue, "Account receiver not found!");
            return;
        }
            
            // Optional<KnownPixModel> knownPixModelExists =
            // knownPixService.existsByIdKeyAndIdAccount(idAccount,
            // pixModelOptional.get().getIdPix());
        
            // if (knownPixModelExists.isPresent()) {
            // return ResponseEntity.status(HttpStatus.NOT_FOUND)
            // .body(knownPixModelExists.get());
            // }
            
            // var knownPixModel = new KnownPixModel();
        
            // long sevenDaysAgoEpoch =
            // java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();
        
            // if (accountReceiveModelOptional.get().getCreatedAt() >= sevenDaysAgoEpoch) {
            // return ResponseEntity.status(HttpStatus.LOCKED)
            // .body("The account that will receive the Pix was created less than 7 days
            // ago.");
            // }

        messagingTemplate.convertAndSendToUser(username, confirmQueue, "Do you want to confirm the payment of R$" + paymentDto.amountPaid() + " ?");;
    }

    @MessageMapping("/confirm")
    public void sendPix(@Payload PaymentConfirmationDto confirmation,@Header("username") String username) {
        if (!confirmation.isConfirm()) {
            messagingTemplate.convertAndSendToUser(username, statusQueue, "Payment Cancelled!");
            return;
        }

        Optional<AccountModel> accountSenderModelOptional = accountService.findById(confirmation.idAccount());
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(confirmation.pixKey());

        if (accountSenderModelOptional.isEmpty() || accountReceiveModelOptional.isEmpty()) {
            messagingTemplate.convertAndSendToUser(username, statusQueue, "Account receiver not found!");
            return;
        }

        var paymentModel = new PaymentModel();

        BeanUtils.copyProperties(confirmation, paymentModel);
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
        accountService.updateBalanceSender(accountSenderModelOptional.get());
        accountService.updateBalanceReceive(accountReceiveModelOptional.get());

        messagingTemplate.convertAndSendToUser(username, statusQueue, paymentModel);
    }
}