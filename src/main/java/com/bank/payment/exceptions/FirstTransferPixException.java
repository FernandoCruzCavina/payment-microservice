package com.bank.payment.exceptions;
/**
 * Exception thrown when a user attempts to make a PIX transfer for the first time to a specific account.
 * This exception extends RuntimeException and provides a default error message indicating that this is the first PIX transfer.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class FirstTransferPixException extends RuntimeException{
    
    public FirstTransferPixException(){
        super("Esta é a primeira vez fazendo pix para essa conta.");
    }
}
