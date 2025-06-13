package com.bank.payment.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.exceptions.AccountReceiverNotFoundException;
import com.bank.payment.exceptions.AccountSenderNotFoundException;
import com.bank.payment.exceptions.FirstTransferPixException;
import com.bank.payment.exceptions.PaymentNotFoundException;
import com.bank.payment.exceptions.PixNotFoundException;
import com.bank.payment.exceptions.TransferBalanceToYourselfException;
import com.bank.payment.exceptions.TransferInsuficientBalanceException;
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
    public PaymentModel findById(Long idPayment) {
        PaymentModel paymentModel = paymentRepository.findById(idPayment)
                .orElseThrow(PaymentNotFoundException::new);
        return paymentModel;
    }

    @Override
    public String delete(Long idPayment) {
        PaymentModel paymentModel = paymentRepository.findById(idPayment)
                .orElseThrow(PaymentNotFoundException::new);

        paymentRepository.delete(paymentModel);
        return "Pagamento deletado com sucesso.";
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
    public String analyzePayment(Long idAccount, String pixKey, PaymentAnalyzeDto paymentAnalyzeDto){
        AccountModel accountSenderModel = accountService.findById(idAccount)
                .orElseThrow(AccountSenderNotFoundException::new);
        AccountModel accountReceiveModel = accountService.findByPixKey(pixKey)
                .orElseThrow(AccountReceiverNotFoundException::new);
        PixModel pixModel = pixService.findByKey(pixKey)
                .orElseThrow(PixNotFoundException::new);

        // if(accountSenderModel.getIdAccount().equals(accountSenderModel.getIdAccount())) {
        //     throw new TransferBalanceToYourselfException();
        // }

        if (accountSenderModel.getBalance().compareTo(paymentAnalyzeDto.amountPaid()) <= 0) {
            throw new TransferInsuficientBalanceException();
        }

        Optional<KnownPixModel> knownPixModelExists = knownPixService.existsByIdAccountAndPixKey(idAccount,
                pixModel.getKey());

        long sevenDaysAgoEpoch = java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();

        System.out.println(sevenDaysAgoEpoch);
        
        if (accountReceiveModel.getCreatedAt() >= sevenDaysAgoEpoch) {
            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(paymentAnalyzeDto.email());
            return "A conta que receberá o dinheiro foi criada a menos de 7 dias atrás. Deseja continuar?";
        }

        if (!knownPixModelExists.isPresent()) {
            var knownPixModel = new KnownPixModel();

            knownPixModel.setIdAccount(accountSenderModel.getIdAccount());
            knownPixModel.setPixKey(pixModel.getKey());
            knownPixService.save(knownPixModel);

            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(paymentAnalyzeDto.email());
            return "Você nunca fez um pix para essa chave, deseja continuar?";
        }
        
        // paymentGenerateCodePublisher.publishEventNewCodeConfirmation(paymentAnalyzeDto.email());
        return "Você realmente deseja fazer esse pagamento?";
    }

    public void sendPix(ConclusionPaymentDto paymentDto){
        var paymentModel = new PaymentModel();
        AccountModel accountSenderModel = accountService.findById(paymentDto.idAccount())
                .orElseThrow(AccountSenderNotFoundException::new);
        AccountModel accountReceiveModel = accountService.findByPixKey(paymentDto.pixKey())
                .orElseThrow(AccountReceiverNotFoundException::new);

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(accountReceiveModel);
        paymentModel.setSenderAccount(accountSenderModel);
        paymentModel.setPaymentType(PaymentType.PIX);

        accountSenderModel.setBalance(accountSenderModel.getBalance().subtract(paymentModel.getAmountPaid()));

        accountReceiveModel.setBalance(accountReceiveModel.getBalance().add(paymentModel.getAmountPaid()));

        System.out.println("Sender: " + accountSenderModel.getIdAccount());
        System.out.println("Receiver: " + accountReceiveModel.getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(accountSenderModel);
        accountService.updateBalanceReceive(accountReceiveModel);
    }

    public String directPayment(Long idAccount, String pixKey, PaymentDto paymentDto){
        var paymentModel = new PaymentModel();

        AccountModel accountSenderModel = accountService.findById(idAccount)
                .orElseThrow(AccountSenderNotFoundException::new);
        AccountModel accountReceiveModel = accountService.findByPixKey(pixKey)
                .orElseThrow(AccountReceiverNotFoundException::new);
        PixModel pixModel = pixService.findByKey(pixKey)
                .orElseThrow(PixNotFoundException::new);

        knownPixService.existsByIdAccountAndPixKey(idAccount, pixModel.getKey())
                .orElseThrow(FirstTransferPixException::new);

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(accountReceiveModel);
        paymentModel.setSenderAccount(accountSenderModel);
        paymentModel.setPaymentType(PaymentType.PIX);

        accountSenderModel.setBalance(accountSenderModel.getBalance().subtract(paymentModel.getAmountPaid()));

        accountReceiveModel.setBalance(accountReceiveModel.getBalance().add(paymentModel.getAmountPaid()));

        System.out.println("Sender: " + accountSenderModel.getIdAccount());
        System.out.println("Receiver: " + accountReceiveModel.getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(accountSenderModel);
        accountService.updateBalanceReceive(accountReceiveModel);

        return "O pagamento foi realizado com sucesso!";
    }
}
