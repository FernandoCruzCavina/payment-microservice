package com.bank.payment.exceptions;

public class TransferBalanceToYourselfException extends RuntimeException{
    
    public TransferBalanceToYourselfException(){
        super("Voce não pode enviar dinheiro pra si mesmo!");
    }
}
