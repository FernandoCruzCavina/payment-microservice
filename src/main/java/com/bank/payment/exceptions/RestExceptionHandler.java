package com.bank.payment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the payment microservice.
 * <p>
 * Handles custom exceptions related to payments, accounts, and Pix operations,
 * returning appropriate HTTP responses with error messages.
 * </p>
 * 
 * <ul>
 *   <li>{@link PaymentNotFoundException} - returns 404 NOT FOUND</li>
 *   <li>{@link PixNotFoundException} - returns 404 NOT FOUND</li>
 *   <li>{@link AccountSenderNotFoundException} - returns 404 NOT FOUND</li>
 *   <li>{@link AccountReceiverNotFoundException} - returns 404 NOT FOUND</li>
 *   <li>{@link FirstTransferPixException} - returns 409 CONFLICT</li>
 *   <li>{@link TransferBalanceToYourselfException} - returns 400 BAD REQUEST</li>
 *   <li>{@link TransferInsuficientBalanceException} - returns 422 UNPROCESSABLE ENTITY</li>
 * </ul>
 * 
 * Each handler returns a {@link MessageHandler} object with the HTTP status and error message.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles PaymentNotFoundException and returns a 404 NOT FOUND response.
     *
     * @param ex the thrown PaymentNotFoundException
     * @return a ResponseEntity containing a MessageHandler with NOT_FOUND status and error message
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountNotFound(PaymentNotFoundException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    /**
     * Handles PixNotFoundException and returns a 404 NOT FOUND response.
     *
     * @param ex the thrown PixNotFoundException
     * @return a ResponseEntity containing a MessageHandler with NOT_FOUND status and error message
     */
    @ExceptionHandler(PixNotFoundException.class)
    public ResponseEntity<MessageHandler> handlePixNotFound(PixNotFoundException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    /**
     * Handles AccountSenderNotFoundException and returns a 404 NOT FOUND response.
     *
     * @param ex the thrown AccountSenderNotFoundException
     * @return a ResponseEntity containing a MessageHandler with NOT_FOUND status and error message
     */
    @ExceptionHandler(AccountSenderNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountSenderNotFound(AccountSenderNotFoundException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    /**
     * Handles AccountReceiverNotFoundException and returns a 404 NOT FOUND response.
     *
     * @param ex the thrown AccountReceiverNotFoundException
     * @return a ResponseEntity containing a MessageHandler with NOT_FOUND status and error message
     */
    @ExceptionHandler(AccountReceiverNotFoundException.class)
    public ResponseEntity<MessageHandler> handleAccountReceiverNotFound(AccountReceiverNotFoundException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageException);
    }

    /**
     * Handles FirstTransferPixException and returns a 409 CONFLICT response.
     *
     * @param ex the thrown FirstTransferPixException
     * @return a ResponseEntity containing a MessageHandler with CONFLICT status and error message
     */
    @ExceptionHandler(FirstTransferPixException.class)
    public ResponseEntity<MessageHandler> handleFirstTransferPix(FirstTransferPixException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageException);
    }

    /**
     * Handles TransferBalanceToYourselfException and returns a 400 BAD REQUEST response.
     *
     * @param ex the thrown TransferBalanceToYourselfException
     * @return a ResponseEntity containing a MessageHandler with BAD_REQUEST status and error message
     */
    @ExceptionHandler(TransferBalanceToYourselfException.class)
    public ResponseEntity<MessageHandler> handleTransferBalanceToYourself(TransferBalanceToYourselfException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageException);
    }

    /**
     * Handles TransferInsuficientBalanceException and returns a 422 UNPROCESSABLE ENTITY response.
     *
     * @param ex the thrown TransferInsuficientBalanceException
     * @return a ResponseEntity containing a MessageHandler with UNPROCESSABLE_ENTITY status and error message
     */
    @ExceptionHandler(TransferInsuficientBalanceException.class)
    public ResponseEntity<MessageHandler> handleTransferInsuficientBalance(TransferInsuficientBalanceException ex) {
        MessageHandler messageException = new MessageHandler(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(messageException);
    }
}
