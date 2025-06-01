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

@Component
public class PixConsumer {
    @Autowired
    PixService pixService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${broker.queue.pixEventQueue}", durable = "true"), exchange = @Exchange(value = "${broker.exchange.pixEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")))
    public void listenPixEvent(@Payload PixEventDto pixEventDto) {
        var pixModel = pixEventDto.convertToPixModel();

        switch (ActionType.valueOf(pixEventDto.getActionType())) {
            case CREATE:
                pixService.save(pixModel);
                break;

            default:
                break;
        }
    }
}
