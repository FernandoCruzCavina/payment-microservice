package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.AccountEventDto;
import com.bank.payment.enums.ActionType;

@Component
public class PaymentReceiverEventPublisher {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.exchange.paymentReceiveExchange}")
    private String exchangeAccountEvent;

    public void publishPaymentReceiverEvent(AccountEventDto accountEventDto, ActionType actionType) {
        accountEventDto.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeAccountEvent, "", accountEventDto);
    }
}
