package com.securedoc.docmanagerservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Cette méthode crée automatiquement la queue "scan-queue" dans RabbitMQ au démarrage
    @Bean
    public Queue scanQueue() {
        return new Queue("scan-queue", true); // true = durable (ne disparait pas au redémarrage)
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}