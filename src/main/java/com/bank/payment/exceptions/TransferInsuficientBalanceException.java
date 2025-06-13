package com.bank.payment.exceptions;

public class TransferInsuficientBalanceException extends RuntimeException {
    
    public TransferInsuficientBalanceException(){
        super("Insuficiente saldo para completar o pix");
    }
}
