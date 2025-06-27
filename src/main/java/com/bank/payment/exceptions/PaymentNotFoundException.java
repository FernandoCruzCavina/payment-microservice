package com.bank.payment.exceptions;
/**
 * Exception thrown when a payment is not found.
 * This exception extends RuntimeException and provides a default error message indicating that the payment was not found.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(){
        super("Pagamento não foi encontrado.");
    }
}
