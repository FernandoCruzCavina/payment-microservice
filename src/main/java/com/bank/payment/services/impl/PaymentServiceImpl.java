package com.bank.payment.services.impl;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

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

    private final PaymentRepository paymentRepository;

    private final PaymentEventPublisher paymentEventPublisher;

    private final PaymentGenerateCodePublisher paymentGenerateCodePublisher;

    private final AccountService accountService;

    private final KnownPixService knownPixService;

    private final PixService pixService;
    
    Logger logger = Logger.getLogger(getClass().getName());

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher, PaymentGenerateCodePublisher paymentGenerateCodePublisher ,AccountService accountService, KnownPixService knownPixService, PixService pixService){
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.paymentGenerateCodePublisher = paymentGenerateCodePublisher;
        this.accountService = accountService;
        this.knownPixService = knownPixService;
        this.pixService = pixService;
    }

    @Override
    public PaymentModel findById(Long idPayment) {
        return paymentRepository.findById(idPayment)
                .orElseThrow(PaymentNotFoundException::new);
    }

    @Override
    public String delete(Long idPayment) {
        PaymentModel paymentModel = paymentRepository.findById(idPayment)
                .orElseThrow(PaymentNotFoundException::new);

        paymentRepository.delete(paymentModel);
        return "Pagamento deletado com sucesso.";
    }

    @Transactional
    @Override
    public PaymentModel savePayment(PaymentModel paymentModel) {
        paymentRepository.save(paymentModel);

        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());
        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());

        return paymentModel;
    }

    @Override
    public String reviewPaymentBeforeProcessing(Long idAccount, String pixKey, PaymentAnalyzeDto paymentAnalyzeDto){
        AccountModel accountSenderModel = accountService.findById(idAccount)
                .orElseThrow(AccountSenderNotFoundException::new);
        AccountModel accountReceiveModel = accountService.findByPixKey(pixKey)
                .orElseThrow(AccountReceiverNotFoundException::new);
        PixModel pixModel = pixService.findByKey(pixKey)
                .orElseThrow(PixNotFoundException::new);

        if(accountSenderModel.getIdAccount()==accountReceiveModel.getIdAccount()) {
            throw new TransferBalanceToYourselfException();
        }

        if (accountSenderModel.getBalance().compareTo(paymentAnalyzeDto.amountPaid()) <= 0) {
            throw new TransferInsuficientBalanceException();
        }

        Optional<KnownPixModel> knownPixModelExists = knownPixService.existsByIdAccountAndPixKey(idAccount,
                pixModel.getKey());

        long sevenDaysAgoEpoch = java.time.Instant.now().minus(java.time.Duration.ofDays(7)).getEpochSecond();

        logger.info("Seven days ago epoch: " + sevenDaysAgoEpoch);
        
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

        logger.info("Sender: " + accountSenderModel.getIdAccount());
        logger.info("Receiver: " + accountReceiveModel.getIdAccount());

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

        logger.info("Sender: " + accountSenderModel.getIdAccount());
        logger.info("Receiver: " + accountReceiveModel.getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(accountSenderModel);
        accountService.updateBalanceReceive(accountReceiveModel);

        return "O pagamento foi realizado com sucesso!";
    }
}
