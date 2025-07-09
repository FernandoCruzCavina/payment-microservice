package com.bank.payment.services.impl;

import static com.bank.payment.utils.PaymentValidations.validateNotTransferringToSelf;
import static com.bank.payment.utils.PaymentValidations.validateSufficientBalance;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.exceptions.AccountReceiverNotFoundException;
import com.bank.payment.exceptions.AccountSenderNotFoundException;
import com.bank.payment.exceptions.FirstTransferPixException;
import com.bank.payment.exceptions.PaymentNotFoundException;
import com.bank.payment.models.AccountModel;
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
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private static final String MESSAGE_NEW_ACCOUNT = "A conta que receberá o dinheiro foi criada a menos de 7 dias atrás. Deseja continuar?";
    private static final String MESSAGE_FIRST_TIME_PIX = "Você nunca fez um pix para essa chave, deseja continuar?";
    private static final String MESSAGE_DEFAULT_CONFIRMATION = "Você realmente deseja fazer esse pagamento?";


    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher, PaymentGenerateCodePublisher paymentGenerateCodePublisher ,AccountService accountService, KnownPixService knownPixService, PixService pixService){
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.paymentGenerateCodePublisher = paymentGenerateCodePublisher;
        this.accountService = accountService;
        this.knownPixService = knownPixService;
        this.pixService = pixService;
    }

    @Override
    public PaymentModel findByIdOrThrow(Long idPayment) {
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
    public String reviewPaymentBeforeProcessing(Long senderAccountId, String pixKey, PaymentAnalyzeDto paymentAnalyzeDto){
        AccountModel senderAccountModel = accountService.findByIdOrThrow(senderAccountId, AccountSenderNotFoundException::new);
        AccountModel receiverAccountModel = accountService.findByPixKeyOrThrow(pixKey, AccountReceiverNotFoundException::new);
        PixModel receiverPixModel = pixService.findByKeyOrThrow(pixKey);

        validateNotTransferringToSelf(senderAccountModel, receiverAccountModel);
        validateSufficientBalance(senderAccountModel, paymentAnalyzeDto.amountPaid());
        
        if (knownPixService.wasReceiverAccountCreatedLessThan7DaysAgo(receiverAccountModel)) {
            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(paymentAnalyzeDto.email());
            return MESSAGE_NEW_ACCOUNT;
        }

        if (knownPixService.isTheFirstTransaction(senderAccountModel, receiverPixModel)) {
            knownPixService.saveKnownPixModel(senderAccountModel, receiverPixModel);
            paymentGenerateCodePublisher.publishEventNewCodeConfirmation(paymentAnalyzeDto.email());
            return MESSAGE_FIRST_TIME_PIX;
        }
        
        return MESSAGE_DEFAULT_CONFIRMATION;
    }
    
    public void sendPix(ConclusionPaymentDto paymentDto){
        var paymentModel = new PaymentModel();
        AccountModel senderAccountModel = accountService.findByIdOrThrow(paymentDto.idAccount(), AccountSenderNotFoundException::new);
        AccountModel receiverAccountModel = accountService.findByPixKeyOrThrow(paymentDto.pixKey(), AccountReceiverNotFoundException::new);

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(receiverAccountModel);
        paymentModel.setSenderAccount(senderAccountModel);
        paymentModel.setPaymentType(PaymentType.PIX);

        senderAccountModel.setBalance(senderAccountModel.getBalance().subtract(paymentModel.getAmountPaid()));
        receiverAccountModel.setBalance(receiverAccountModel.getBalance().add(paymentModel.getAmountPaid()));

        logger.info("Sender: {}", senderAccountModel.getIdAccount());
        logger.info("Receiver: {}", receiverAccountModel.getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(senderAccountModel);
        accountService.updateBalanceReceive(receiverAccountModel);
    }

    public String directPayment(Long idAccount, String pixKey, PaymentDto paymentDto){
        var paymentModel = new PaymentModel();

        AccountModel senderAccountModel = accountService.findByIdOrThrow(idAccount, AccountSenderNotFoundException::new);
        AccountModel receiverAccountModel = accountService.findByPixKeyOrThrow(pixKey, AccountReceiverNotFoundException::new);
        PixModel pixModel = pixService.findByKeyOrThrow(pixKey);

        knownPixService.existsByIdAccountAndPixKey(idAccount, pixModel.getKey())
                .orElseThrow(FirstTransferPixException::new);

        BeanUtils.copyProperties(paymentDto, paymentModel);
        paymentModel.setPaymentRequestDate(new Date().getTime());
        paymentModel.setPaymentCompletionDate(new Date().getTime());
        paymentModel.setReceiverAccount(receiverAccountModel);
        paymentModel.setSenderAccount(senderAccountModel);
        paymentModel.setPaymentType(PaymentType.PIX);

        senderAccountModel.setBalance(senderAccountModel.getBalance().subtract(paymentModel.getAmountPaid()));
        receiverAccountModel.setBalance(receiverAccountModel.getBalance().add(paymentModel.getAmountPaid()));

        logger.info("Sender: {}", senderAccountModel.getIdAccount());
        logger.info("Receiver: {}", receiverAccountModel.getIdAccount());

        savePayment(paymentModel);
        accountService.updateBalanceSender(senderAccountModel);
        accountService.updateBalanceReceive(receiverAccountModel);

        return "O pagamento foi realizado com sucesso!";
    }
}
