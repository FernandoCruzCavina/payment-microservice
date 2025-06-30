package com.bank.payment.configs;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
/**
 * Configuration class for RabbitMQ setup in the payment microservice.
 * <p>
 * This class defines the RabbitMQ connection factory, message converter, exchanges, and queues used in the payment microservice.
 * It uses Jackson for JSON message conversion and sets up fanout exchanges for different event types.
 * 
 * @author Fernando Cruz Cavina
 * @version 1.0.0, 06/26/2025
 * @since 1.0.0
 */
@Configuration
public class RabbitmqConfig {

    @Value(value = "${broker.exchange.accountEventExchange}")
    private String exchangeAccountEvent;

    @Value(value = "${broker.exchange.paymentSenderExchange}")
    private String exchangePaymentSender;

    @Value(value = "${broker.exchange.paymentSenderExchange}")
    private String exchangePaymentReceiver;

    @Value(value = "${broker.exchange.paymentEventExchange}")
    private String exchangePayment;

    @Value("${broker.queue.requestNewCode}")
    public String exchangeNewCodeQueue;

    @Value("${broker.queue.sendPayment}")
    public String exchangeSendPaymentQueue;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(new CachingConnectionFactory());
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public FanoutExchange fanoutExchangeAccount() {
        return new FanoutExchange(exchangeAccountEvent);
    }

    @Bean
    public FanoutExchange fanoutExchangePayment() {
        return new FanoutExchange(exchangePayment);
    }

    @Bean
    public FanoutExchange fanoutExchangePaymentReceive() {
        return new FanoutExchange(exchangePaymentReceiver);
    }

    @Bean
    public FanoutExchange fanoutExchangePaymentSender() {
        return new FanoutExchange(exchangePaymentSender);
    }

    @Bean
    public Queue requestNewCodeQueue() {
        return new Queue(exchangeNewCodeQueue, true);
    }

    @Bean
    public Queue sendPaymentQueue() {
        return new Queue(exchangeSendPaymentQueue, true);
    }
}
