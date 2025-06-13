package com.bank.payment.exceptions;

public class PixNotFoundException extends RuntimeException {
    
    public PixNotFoundException(){
        super("O pix não foi encontrado");
    }
}
