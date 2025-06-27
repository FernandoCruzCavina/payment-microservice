package com.bank.payment.exceptions;

/**
 * Exception thrown when there is insufficient balance to complete a transfer.
 * This exception extends RuntimeException and provides a default error message indicating insufficient balance.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class TransferInsuficientBalanceException extends RuntimeException {
    
    public TransferInsuficientBalanceException(){
        super("Insuficiente saldo para completar o pix");
    }
}
