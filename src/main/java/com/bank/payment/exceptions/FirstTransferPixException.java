package com.bank.payment.exceptions;

public class FirstTransferPixException extends RuntimeException{
    
    public FirstTransferPixException(){
        super("Esta é a primeira vez fazendo pix para essa conta.");
    }
}
