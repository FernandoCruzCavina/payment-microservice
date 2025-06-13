package com.bank.payment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountNotFound(PaymentNotFoundException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    @ExceptionHandler(PixNotFoundException.class)
    public ResponseEntity<MessageHandler> handlePixNotFound(PixNotFoundException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    @ExceptionHandler(AccountSenderNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountSenderNotFound(AccountSenderNotFoundException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    @ExceptionHandler(AccountReceiverNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountReceiverNotFound(AccountReceiverNotFoundException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    @ExceptionHandler(FirstTransferPixException.class)
    public ResponseEntity<MessageHandler> handleFirstTransferPix(FirstTransferPixException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.CONFLICT, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageException);
    }

    @ExceptionHandler(TransferBalanceToYourselfException.class)
    public ResponseEntity<MessageHandler> handleTransferBalanceToYourself(TransferBalanceToYourselfException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.BAD_REQUEST, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageException);
    }

    @ExceptionHandler(TransferInsuficientBalanceException.class)
    public ResponseEntity<MessageHandler> handleTransferInsuficientBalance(TransferInsuficientBalanceException ex) {

        MessageHandler messageException = new MessageHandler(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(messageException);
    }
}
