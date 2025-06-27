package com.bank.payment.consumers;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.PixEventDto;
import com.bank.payment.enums.ActionType;
import com.bank.payment.services.PixService;

/**
 * Consumer for Pix-related events received from account microservice via RabbitMQ.
 * <p>
 * Listens to the Pix event queue and processes Pix creation events,
 * converting the received DTO to a model and delegating to the {@link PixService}.
 * </p>
 * 
 * <ul>
 *   <li>Queue: <b>${broker.queue.pixEventQueue}</b></li>
 *   <li>Exchange: <b>${broker.exchange.pixEventExchange}</b> (type: FANOUT)</li>
 * </ul>
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class PixConsumer {
    @Autowired
    PixService pixService;

    /**
     * Listens for Pix events and saves them according to the action type.
     *
     * @param pixEventDto the Pix event data received from the queue
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${broker.queue.pixEventQueue}", durable = "true"), exchange = @Exchange(value = "${broker.exchange.pixEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")))
    public void listenPixEvent(@Payload PixEventDto pixEventDto) {
        var pixModel = pixEventDto.toPixModel();

        switch (ActionType.valueOf(pixEventDto.getActionType())) {
            case CREATE:
                pixService.save(pixModel);
                break;
            default:
                break;
        }
    }
}
