package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.PaymentEventDto;

/**
 * Publisher for sending payment-related events to account microservice via RabbitMQ.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class PaymentEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;

    public PaymentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    /**
     * Publishes a payment event to the payment event exchange.
     *
     * @param paymentEventDto the payment event data
     */
    public void publishPaymentEvent(PaymentEventDto paymentEventDto) {
        rabbitTemplate.convertAndSend(exchangePaymentEvent, "", paymentEventDto);
    }
}
