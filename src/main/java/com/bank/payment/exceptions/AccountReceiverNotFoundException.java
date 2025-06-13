package com.bank.payment.exceptions;

public class AccountReceiverNotFoundException extends RuntimeException {
    
    public AccountReceiverNotFoundException(){
        super("A conta que receberá não foi encontrada");
    }
}
