package com.bank.payment.exceptions;

/**
 * Exception thrown when a user attempts to transfer balance to themselves.
 * This exception extends RuntimeException and provides a default error message.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class TransferBalanceToYourselfException extends RuntimeException{
    
    public TransferBalanceToYourselfException(){
        super("Voce não pode enviar dinheiro pra si mesmo!");
    }
}
