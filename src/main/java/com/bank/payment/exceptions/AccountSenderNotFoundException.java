package com.bank.payment.exceptions;

public class AccountSenderNotFoundException extends RuntimeException {
    
    public AccountSenderNotFoundException(){
        super("A conta que enviará não foi encontrada");
    }
}
