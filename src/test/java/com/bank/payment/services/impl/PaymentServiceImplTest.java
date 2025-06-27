package com.bank.payment.services.impl;

import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.enums.PaymentType;
import com.bank.payment.exceptions.*;
import com.bank.payment.models.AccountModel;
import com.bank.payment.models.KnownPixModel;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

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
    void findById_shouldThrowPaymentNotFoundException() {
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
        Long idAccount = 1L;
        String pixKey = "pixkey";
        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email@test.com", BigDecimal.TEN, "Pagamento de teste");

        AccountModel sender = new AccountModel();
        sender.setIdAccount(idAccount);
        sender.setBalance(BigDecimal.valueOf(100));
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);
        receiver.setCreatedAt(java.time.Instant.now().minus(java.time.Duration.ofDays(10)).getEpochSecond());

        PixModel pixModel = new PixModel();
        pixModel.setKey(pixKey);

        when(accountService.findById(idAccount)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(pixKey)).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(pixKey)).thenReturn(Optional.of(pixModel));
        when(knownPixService.existsByIdAccountAndPixKey(idAccount, pixKey)).thenReturn(Optional.of(new KnownPixModel()));

        String result = paymentService.reviewPaymentBeforeProcessing(idAccount, pixKey, dto);

        assertEquals("Você realmente deseja fazer esse pagamento?", result);
    }

    // --- Exception test: sender not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowAccountSenderNotFoundException() {
        when(accountService.findById(anyLong())).thenReturn(Optional.empty());

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email", BigDecimal.TEN, "desc");
        assertThrows(AccountSenderNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(1L, "pix", dto));
    }

    // --- Exception test: receiver not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowAccountReceiverNotFoundException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        when(accountService.findById(1L)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(anyString())).thenReturn(Optional.empty());

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email", BigDecimal.TEN, "desc");
        assertThrows(AccountReceiverNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(1L, "pix", dto));
    }

    // --- Exception test: pix not found ---
    @Test
    void reviewPaymentBeforeProcessing_shouldThrowPixNotFoundException() {
        AccountModel sender = new AccountModel();
        sender.setIdAccount(1L);
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);

        when(accountService.findById(1L)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(anyString())).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(anyString())).thenReturn(Optional.empty());

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email", BigDecimal.TEN, "desc");
        assertThrows(PixNotFoundException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(1L, "pix", dto));
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

        when(accountService.findById(1L)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(anyString())).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(anyString())).thenReturn(Optional.of(pixModel));

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email", BigDecimal.TEN, "desc");
        assertThrows(TransferBalanceToYourselfException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(1L, "pix", dto));
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

        when(accountService.findById(1L)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(anyString())).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(anyString())).thenReturn(Optional.of(pixModel));

        PaymentAnalyzeDto dto = new PaymentAnalyzeDto("email", BigDecimal.TEN, "desc");
        assertThrows(TransferInsuficientBalanceException.class,
                () -> paymentService.reviewPaymentBeforeProcessing(1L, "pix", dto));
    }

    // --- Exception test: first transfer to pix ---
    @Test
    void directPayment_shouldThrowFirstTransferPixException() {
        Long idAccount = 1L;
        String pixKey = "pixkey";
        PaymentDto paymentDto = new PaymentDto("descricao do pagamento", BigDecimal.TEN);

        AccountModel sender = new AccountModel();
        sender.setIdAccount(idAccount);
        sender.setBalance(BigDecimal.valueOf(100));
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);

        PixModel pixModel = new PixModel();
        pixModel.setKey(pixKey);

        when(accountService.findById(idAccount)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(pixKey)).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(pixKey)).thenReturn(Optional.of(pixModel));
        when(knownPixService.existsByIdAccountAndPixKey(idAccount, pixKey)).thenReturn(Optional.empty());

        assertThrows(FirstTransferPixException.class,
                () -> paymentService.directPayment(idAccount, pixKey, paymentDto));
    }

    // --- Success test for directPayment ---
    @Test
    void directPayment_shouldSucceed() {
        Long idAccount = 1L;
        String pixKey = "pixkey";
        PaymentDto paymentDto = new PaymentDto("descricao do pagamento", BigDecimal.TEN);

        AccountModel sender = new AccountModel();
        sender.setIdAccount(idAccount);
        sender.setBalance(BigDecimal.valueOf(100));
        AccountModel receiver = new AccountModel();
        receiver.setIdAccount(2L);
        receiver.setBalance(BigDecimal.ZERO);

        PixModel pixModel = new PixModel();
        pixModel.setKey(pixKey);

        KnownPixModel knownPix = new KnownPixModel();

        when(accountService.findById(idAccount)).thenReturn(Optional.of(sender));
        when(accountService.findByPixKey(pixKey)).thenReturn(Optional.of(receiver));
        when(pixService.findByKey(pixKey)).thenReturn(Optional.of(pixModel));
        when(knownPixService.existsByIdAccountAndPixKey(idAccount, pixKey)).thenReturn(Optional.of(knownPix));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = paymentService.directPayment(idAccount, pixKey, paymentDto);

        assertEquals("O pagamento foi realizado com sucesso!", result);
        assertEquals(BigDecimal.valueOf(90), sender.getBalance());
        assertEquals(BigDecimal.TEN, receiver.getBalance());
        verify(paymentRepository).save(any());
        verify(accountService).updateBalanceSender(sender);
        verify(accountService).updateBalanceReceive(receiver);
    }
}