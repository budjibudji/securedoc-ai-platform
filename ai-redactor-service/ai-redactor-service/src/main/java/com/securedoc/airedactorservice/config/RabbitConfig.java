package com.securedoc.airedactorservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(); // Convert JSON ↔ DTO
    }

    @Bean
    public Queue scanQueue() {
        return new Queue("scan-queue", true);
    }

    @Bean
    public Queue processedQueue() {
        return new Queue("processed-queue", true);
    }
}
