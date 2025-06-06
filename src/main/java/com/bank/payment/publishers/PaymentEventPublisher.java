package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.PaymentEventDto;

@Component
public class PaymentEventPublisher {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    public void publishPaymentEvent(PaymentEventDto paymentEventDto) {
        rabbitTemplate.convertAndSend(exchangePaymentEvent, "", paymentEventDto);

    }

}
