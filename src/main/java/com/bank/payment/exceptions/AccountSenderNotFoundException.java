package com.bank.payment.exceptions;

/**
 * Exception thrown when the account that is supposed to send a payment is not found.
 * This exception extends RuntimeException and provides a default error message.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class AccountSenderNotFoundException extends RuntimeException {
    
    public AccountSenderNotFoundException(){
        super("A conta que enviará não foi encontrada");
    }
}
