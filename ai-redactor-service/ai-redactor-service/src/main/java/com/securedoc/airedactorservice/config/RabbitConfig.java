package com.securedoc.airedactorservice.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue scanQueue() {
        return new Queue("scan-queue", true); // true = durable
    }


    // On ignore l'avertissement car c'est la seule solution stable actuellement
    @SuppressWarnings("deprecation")
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}