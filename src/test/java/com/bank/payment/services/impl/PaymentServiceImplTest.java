package com.bank.payment.services.impl;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.exceptions.*;
import com.bank.payment.models.AccountModel;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.models.PixModel;
import com.bank.payment.publishers.PaymentEventPublisher;
import com.bank.payment.publishers.PaymentGenerateCodePublisher;
import com.bank.payment.repository.PaymentRepository;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PixService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock 
    private PaymentRepository paymentRepository;
    @Mock 
    private PaymentEventPublisher paymentEventPublisher;
    @Mock 
    private PaymentGenerateCodePublisher paymentGenerateCodePublisher;
    @Mock 
    private AccountService accountService;
    @Mock 
    private KnownPixService knownPixService;
    @Mock 
    private PixService pixService;

    @InjectMocks 
    private PaymentServiceImpl paymentService;

    // --- Success test for findById ---
    @Test
    void findById_shouldReturnPayment() {
        PaymentModel payment = new PaymentModel();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentModel result = paymentService.findById(1L);

        assertEquals(payment, result);
    }

    // --- Exception test for findById ---
    @Test
    void findByIdOrThrow_shouldThrowPaymentNotFoundException() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(1L));
    }

    // --- Success test for savePayment ---
    @Test
    void savePayment_shouldPublishEventsAndReturnPayment() {
        PaymentModel payment = new PaymentModel(Long.valueOf(1L), PaymentType.PIX, "Pagamento de teste", BigDecimal.TEN,
                System.currentTimeMillis(), System.currentTimeMillis() + 1000,
                new AccountModel(), new AccountModel());
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentModel result = paymentService.savePayment(payment);

        assertEquals(payment, result);
        verify(paymentEventPublisher, times(2)).publishPaymentEvent(any());
    }

    // --- Success test for reviewPaymentBeforeProcessing ---
    @Test
    void reviewPaymentBeforeProcessing_shouldReturnConfirmationMessage() {
        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pixkey", "email@test.com", BigDecimal.TEN, "Pagamento de teste");

        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        sender.setBalance(BigDecimal.valueOf(100));
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);
        receiver.setCreatedAt(java.time.Instant.now().minus(java.time.Duration.ofDays(10)).getEpochSecond());

        PixModel pixModel = new PixModel();
        pixModel.setKey("pixkey");

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pixkey")).thenReturn(receiver);
        when(pixService.findByKey("pixkey")).thenReturn(pixModel);
        when(knownPixService.isTheFirstTransaction(any(AccountModel.class), any(PixModel.class))).thenReturn(false);

        String result = paymentService.reviewPaymentBeforeProcessing(dto);

        assertEquals("Você realmente deseja fazer esse pagamento?", result);
    }

    // --- Exception test: sender not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowSenderAccountNotFoundException() {
        when(accountService.findSenderAccountById(anyLong())).thenThrow(SenderAccountNotFoundException.class);

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pix", "email", BigDecimal.TEN, "desc");
        assertThrows(SenderAccountNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(dto));
    }

    // --- Exception test: receiver not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowReceiverAccountNotFoundException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pix")).thenThrow(ReceiverAccountNotFoundException.class);

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pix", "email", BigDecimal.TEN, "desc");
        assertThrows(ReceiverAccountNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(dto));
    }

    // --- Exception test: pix not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowPixNotFoundException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pix")).thenReturn(receiver);
        when(pixService.findByKey("pix")).thenThrow(PixNotFoundException.class);

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pix", "email", BigDecimal.TEN, "desc");
        assertThrows(PixNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(dto));
    }

    // --- Exception test: transfer to yourself ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowTransferBalanceToYourselfException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(1L);

        PixModel pixModel = new PixModel();
        pixModel.setKey("pix");

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pix")).thenReturn(receiver);
        when(pixService.findByKey("pix")).thenReturn(pixModel);

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pix", "email", BigDecimal.TEN, "desc");
        assertThrows(TransferBalanceToYourselfException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(dto));
    }

    // --- Exception test: insufficient balance ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowTransferInsuficientBalanceException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        sender.setBalance(BigDecimal.ZERO);
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);

        PixModel pixModel = new PixModel();
        pixModel.setKey("pix");

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pix")).thenReturn(receiver);
        when(pixService.findByKey("pix")).thenReturn(pixModel);

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto(1L, "pix", "email", BigDecimal.TEN, "desc");

        assertThrows(TransferInsuficientBalanceException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(dto));
    }

    // --- Success test for sendPix ---
    @Test
    void sendPix_shouldSucceed() {
        ConclusionPaymentDto paymentDto = new ConclusionPaymentDto(1L, "pixkey", "desc", BigDecimal.TEN);

        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        sender.setBalance(BigDecimal.valueOf(100));
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);
        receiver.setBalance(BigDecimal.ZERO);

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pixkey")).thenReturn(receiver);
        when(paymentRepository.save(any(PaymentModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountService.updateBalanceSender(any(AccountModel.class))).thenReturn(sender);
        when(accountService.updateBalanceReceiver(any(AccountModel.class))).thenReturn(receiver);
        doNothing().when(paymentEventPublisher).publishPaymentEvent(any());

        String result = paymentService.sendPix(paymentDto);

        assertEquals("O pagamento foi realizado com sucesso!", result);
        assertEquals(BigDecimal.valueOf(90), sender.getBalance());
        assertEquals(BigDecimal.TEN, receiver.getBalance());
        verify(paymentRepository).save(any());
        verify(accountService).updateBalanceSender(sender);
        verify(accountService).updateBalanceReceiver(receiver);
    }

    // --- Exception test: sendPix with sender not found ---
    @Test
    void sendPix_shouldThrowSenderAccountNotFoundException() {
        ConclusionPaymentDto paymentDto = new ConclusionPaymentDto(1L, "pixkey", "desc", BigDecimal.TEN);

        when(accountService.findSenderAccountById(1L)).thenThrow(SenderAccountNotFoundException.class);

        assertThrows(SenderAccountNotFoundException.class, () -> paymentService.sendPix(paymentDto));
    }

    // --- Exception test: sendPix with receiver not found ---
    @Test
    void sendPix_shouldThrowReceiverAccountNotFoundException() {
        ConclusionPaymentDto paymentDto = new ConclusionPaymentDto(1L, "pixkey", "desc", BigDecimal.TEN);

        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);

        when(accountService.findSenderAccountById(1L)).thenReturn(sender);
        when(accountService.findReceiverAccountByPixKey("pixkey")).thenThrow(ReceiverAccountNotFoundException.class);

        assertThrows(ReceiverAccountNotFoundException.class, () -> paymentService.sendPix(paymentDto));
    }
}