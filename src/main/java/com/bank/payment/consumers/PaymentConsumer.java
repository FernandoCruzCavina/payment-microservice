package com.bank.payment.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.services.PaymentService;

@Component
public class PaymentConsumer {
    
    @Autowired
    PaymentService paymentService;
    
    @RabbitListener(queues = "${broker.queue.sendPayment}")
    public void sendPix(@Payload ConclusionPaymentDto paymentDto){
        paymentService.sendPix(paymentDto);
    }
}
