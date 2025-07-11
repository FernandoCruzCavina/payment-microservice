package com.bank.payment.services.impl;

import static com.bank.payment.utils.PaymentValidations.validateNotTransferringToSelf;
import static com.bank.payment.utils.PaymentValidations.validateSufficientBalance;
import static com.bank.payment.utils.PaymentValidations.wasReceiverAccountCreatedLessThan7DaysAgo;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
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
    private static final String MESSAGE_CONCLUSION_PAYMENT = "O pagamento foi realizado com sucesso!";
    private static final String MESSAGE_PAYMENT_DELETED_SUCCESSFULLY = "Pagamento deletado com sucesso.";


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
        return MESSAGE_PAYMENT_DELETED_SUCCESSFULLY;
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
    public String reviewPaymentBeforeProcessing(PaymentAnalyzeDto paymentAnalyzeDto){
        AccountModel senderAccountModel = getSenderAccount(paymentAnalyzeDto.senderAccountId());
        AccountModel receiverAccountModel = getReceiverAccount(paymentAnalyzeDto.receiverPixKey());
        PixModel receiverPixModel = pixService.findByKey(paymentAnalyzeDto.receiverPixKey());

        validateNotTransferringToSelf(senderAccountModel, receiverAccountModel);
        validateSufficientBalance(senderAccountModel, paymentAnalyzeDto.amountPaid());
        
        if (wasReceiverAccountCreatedLessThan7DaysAgo(receiverAccountModel)) {
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
    
    @Transactional
    @Override
    public String sendPix(ConclusionPaymentDto paymentDto){
        AccountModel senderAccountModel = getSenderAccount(paymentDto.senderAccountId());
        AccountModel receiverAccountModel = getReceiverAccount(paymentDto.receiverPixKey());
        
        PaymentModel paymentModel = buildPixPaymentModel(paymentDto, senderAccountModel, receiverAccountModel);

        updateBalances(senderAccountModel, receiverAccountModel, paymentModel.getAmountPaid());

        completePaymentAndUpdateAccounts(paymentModel);
        
        logger.info("Sender: {}", senderAccountModel.getIdAccount());
        logger.info("Receiver: {}", receiverAccountModel.getIdAccount());

        return MESSAGE_CONCLUSION_PAYMENT;
    }

    private PaymentModel buildPixPaymentModel(ConclusionPaymentDto paymentDto, AccountModel senderAccountModel, AccountModel receiverAccountModel) {
        return PaymentModel.fromConclusionPaymentDto(paymentDto, senderAccountModel, receiverAccountModel);
    }

    private void updateBalances(AccountModel senderAccountModel, AccountModel receiverAccountModel, BigDecimal amountPaid) {
        senderAccountModel.setBalance(senderAccountModel.getBalance().subtract(amountPaid));
        receiverAccountModel.setBalance(receiverAccountModel.getBalance().add(amountPaid));
    }

    private void completePaymentAndUpdateAccounts(PaymentModel paymentModel) {
        savePayment(paymentModel);
        accountService.updateBalanceSender(paymentModel.getSenderAccount());
        accountService.updateBalanceReceiver(paymentModel.getReceiverAccount());
    }

    private AccountModel getSenderAccount(Long id) {
        return accountService.findSenderAccountById(id);
    }

    private AccountModel getReceiverAccount(String pixKey) {
        return accountService.findReceiverAccountByPixKey(pixKey);
    }

}