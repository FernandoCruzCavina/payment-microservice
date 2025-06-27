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

/**
 * Consumer for account-related events received from account microservice via RabbitMQ.
 * <p>
 * Listens to the account event queue and processes account creation and update events,
 * converting the received DTO to a model and delegating to the {@link AccountService}.
 * </p>
 * 
 * <ul>
 *   <li>Queue: <b>${broker.queue.accountEventQueue}</b></li>
 *   <li>Exchange: <b>${broker.exchange.accountEventExchange}</b> (type: FANOUT)</li>
 * </ul>
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class AccountConsumer {

    @Autowired
    AccountService accountService;

    /**
     * Listens for account events and saves them according to the action type.
     *
     * @param accountEventDto the account event data received from the queue
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${broker.queue.accountEventQueue}", durable = "true"), exchange = @Exchange(value = "${broker.exchange.accountEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")))
    public void listenAccountEvent(@Payload AccountEventDto accountEventDto) {
        var accountModel = accountEventDto.toAccountModel();

        switch (ActionType.valueOf(accountEventDto.getActionType())) {
            case CREATE:
                accountService.save(accountModel);
                break;
            case UPDATE:
                accountService.save(accountModel);
                break;
            default:
                break;
        }
        System.out.println(accountModel);
    }
}
