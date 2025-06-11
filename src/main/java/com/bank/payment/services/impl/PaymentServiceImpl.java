package com.bank.payment.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.publishers.PaymentEventPublisher;
import com.bank.payment.publishers.PaymentGenerateCodePublisher;
import com.bank.payment.repository.PaymentRepository;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

    @Autowired
    PaymentGenerateCodePublisher paymentGenerateCodePublisher;

    @Autowired
    AccountService accountService;

    @Autowired
    KnownPixService knownPixService;

    @Autowired
    PixService pixService;

    @Override
    public List<PaymentModel> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<PaymentModel> findById(Long idPayment) {
        return paymentRepository.findById(idPayment);
    }

    @Override
    public void delete(PaymentModel paymentModel) {
        paymentRepository.delete(paymentModel);
    }

    @Override
    public PaymentModel save(PaymentModel paymentModel) {
        return paymentRepository.save(paymentModel);
    }

    @Transactional
    @Override
    public PaymentModel savePayment(PaymentModel paymentModel) {
        save(paymentModel);

        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());
        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());

        return paymentModel;
    }

    @Override
    public String analyzePayment(Long idAccount, String pixKey, String email){
        Optional<AccountModel> accountSenderModelOptional = accountService.findById(idAccount);
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(pixKey);
        Optional<PixModel> pixModelOptional = pixService.findByKey(pixKey);
        System.out.println(email + "\n");
        if (!accountSenderModelOptional.isPresent()) {
            return "Account sender not found!";
        }

        if (!pixModelOptional.isPresent()) {
            return "Pix not found!";
        }

        if (!accountReceiveModelOptional.isPresent()) {
            return "Account receiver not found!";
        }
        Optional<KnownPixModel> knownPixModelExists = knownPixService.existsByIdAccountAndPixKey(idAccount,
                pixModelOptional.get().getKey());

        long sevenDaysAgoEpoch = java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();

        System.out.println(sevenDaysAgoEpoch);
        
        if (accountReceiveModelOptional.get().getCreatedAt() >= sevenDaysAgoEpoch) {
            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(email);
            return "The account that will receive the Pix was created less than 7 days ago.";
        }

        if (!knownPixModelExists.isPresent()) {
            var knownPixModel = new KnownPixModel();

            knownPixModel.setIdAccount(accountSenderModelOptional.get().getIdAccount());
            knownPixModel.setPixKey(pixModelOptional.get().getKey());
            knownPixService.save(knownPixModel);

            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(email);
            return "Você nunca fez um pix para essa chave, deseja continuar?";
        }
        
        paymentGenerateCodePublisher.publishEventNewCodeConfirmation(email);
        return "Você realmente deseja fazer esse pagamento?";
    }

    public void sendPix(ConclusionPaymentDto paymentDto){
        var paymentModel = new PaymentModel();
        Optional<AccountModel> accountSenderModelOptional = accountService.findById(paymentDto.idAccount());
        Optional<AccountModel> accountReceiveModelOptional = accountService.findByPixKey(paymentDto.pixKey());
        // Optional<PixModel> pixModelOptional = pixService.findByKey(pixKey);

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(accountReceiveModelOptional.get());
        paymentModel.setSenderAccount(accountSenderModelOptional.get());
        paymentModel.setPaymentType(PaymentType.PIX);

        accountSenderModelOptional.get()
                .setBalance(accountSenderModelOptional.get().getBalance().subtract(paymentModel.getAmountPaid()));

        accountReceiveModelOptional.get()
                .setBalance(accountReceiveModelOptional.get().getBalance().add(paymentModel.getAmountPaid()));

        System.out.println("Sender: " + accountSenderModelOptional.get().getIdAccount());
        System.out.println("Receiver: " + accountReceiveModelOptional.get().getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(accountSenderModelOptional.get());
        accountService.updateBalanceReceive(accountReceiveModelOptional.get());
    }

}
