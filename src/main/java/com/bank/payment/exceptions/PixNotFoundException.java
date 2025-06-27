package com.bank.payment.exceptions;

/**
 * Exception thrown when a Pix key is not found.
 * This exception extends RuntimeException and provides a default error message indicating that the Pix was not found.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class PixNotFoundException extends RuntimeException {
    
    public PixNotFoundException(){
        super("O pix não foi encontrado");
    }
}
