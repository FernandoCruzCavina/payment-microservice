package com.bank.payment.consumers;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.AccountEventDto;
import com.bank.payment.enums.ActionType;
import com.bank.payment.services.AccountService;

@Component
public class AccountConsumer {

    @Autowired
    AccountService accountService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${broker.queue.accountEventQueue}", durable = "true"), exchange = @Exchange(value = "${broker.exchange.accountEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")))
    public void listenAccountEvent(@Payload AccountEventDto accountEventDto) {
        var accountModel = accountEventDto.convertToAccountModel();

        switch (ActionType.valueOf(accountEventDto.getActionType())) {
            case CREATE:
                accountService.save(accountModel);
                break;

            default:
                break;
        }
    }

}
