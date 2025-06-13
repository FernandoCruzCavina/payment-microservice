package com.bank.payment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter

public class MessageHandler extends ResponseEntityExceptionHandler {

    private HttpStatus status;
    private String message;
}