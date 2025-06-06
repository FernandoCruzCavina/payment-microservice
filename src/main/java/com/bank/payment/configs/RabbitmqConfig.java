package com.bank.payment.configs;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitmqConfig {
    @Autowired
    CachingConnectionFactory cachingConnectionFactory;

    @Value(value = "${broker.exchange.accountEventExchange}")
    private String exchangeAccountEvent;

    @Value(value = "${broker.exchange.paymentSenderExchange}")
    private String exchangePaymentSender;

    @Value(value = "${broker.exchange.paymentSenderExchange}")
    private String exchangePaymentReceiver;

    @Value(value = "${broker.exchange.paymentEventExchange}")
    private String exchangePayment;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
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
}
