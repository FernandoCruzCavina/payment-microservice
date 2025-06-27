package com.bank.payment.publishers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.AccountEventDto;
import com.bank.payment.enums.ActionType;

/**
 * Publisher for sending account-related events to RabbitMQ.
 * Publishes events to the account event exchange for account creation and updates.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class AccountEventPublisher {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.exchange.accountEventExchange}")
    private String exchangeAccountEvent;

    /**
     * Publishes an account event to the account event exchange.
     *
     * @param accountEventDto the account event data
     * @param actionType the type of action (CREATE, UPDATE, etc.)
     */
    public void publishAccountEvent(AccountEventDto accountEventDto, ActionType actionType) {
        accountEventDto.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeAccountEvent, "", accountEventDto);
    }
}
