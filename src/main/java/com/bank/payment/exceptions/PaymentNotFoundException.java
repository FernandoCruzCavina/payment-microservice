package com.bank.payment.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(){
        super("Pagamento não foi encontrado.");
    }
}
