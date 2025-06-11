package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentGenerateCodePublisher {
    
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.requestNewCode}")
    private String routingKey;

    public void publishEventNewCodeConfirmation(String email){
        rabbitTemplate.convertAndSend("", routingKey, email);
    }
}
