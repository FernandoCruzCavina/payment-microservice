package com.bank.payment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to handle messages for exceptions in the payment microservice.
 * It extends ResponseEntityExceptionHandler to provide a consistent response structure.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@AllArgsConstructor
@Setter
@Getter
public class MessageHandler extends ResponseEntityExceptionHandler {

    private HttpStatus status;
    private String message;
}