package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publisher for sending email to generate a new confirmation code for payments.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class PaymentGenerateCodePublisher {
    
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.requestNewCode}")
    private String routingKey;

    /**
     * Publishes an event to request a new confirmation code for the given email.
     *
     * @param email the email address to send the confirmation code to
     */
    public void publishEventNewCodeConfirmation(String email){
        rabbitTemplate.convertAndSend("", routingKey, email);
    }
}
