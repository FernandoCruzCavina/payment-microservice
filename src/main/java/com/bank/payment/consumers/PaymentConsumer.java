package com.bank.payment.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bank.payment.dtos.ConclusionPaymentDto;
import com.bank.payment.services.PaymentService;

/**
 * Consumer for payment-related events received from auth microservice via RabbitMQ.
 * <p>
 * Listens to the payment queue and processes payment conclusion events,
 * delegating the processing to the {@link PaymentService}.
 * </p>
 * 
 * <ul>
 *   <li>Queue: <b>${broker.queue.sendPayment}</b></li>
 * </ul>
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Component
public class PaymentConsumer {
    
    private final PaymentService paymentService;

    public PaymentConsumer(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    /**
     * Listens for confirmation of payment and sends the Pix payment.
     *
     * @param paymentDto the payment conclusion data received from the queue
     */
    @RabbitListener(queues = "${broker.queue.sendPayment}")
    public void sendPix(@Payload ConclusionPaymentDto paymentDto){
        paymentService.sendPix(paymentDto);
    }
}
