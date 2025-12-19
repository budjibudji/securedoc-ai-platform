package com.securedoc.docmanagerservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // =================================================
    // 1. LE DIFFUSEUR (Exchange) ET SES FILES
    // =================================================
    @Bean
    public FanoutExchange docExchange() {
        return new FanoutExchange("doc-exchange");
    }

    @Bean
    public Queue scanQueue() {
        return new Queue("scan-queue", true);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue("audit-queue", true);
    }

    @Bean
    public Binding bindScan(Queue scanQueue, FanoutExchange docExchange) {
        return BindingBuilder.bind(scanQueue).to(docExchange);
    }

    @Bean
    public Binding bindAudit(Queue auditQueue, FanoutExchange docExchange) {
        return BindingBuilder.bind(auditQueue).to(docExchange);
    }

    // =================================================
    // 2. FILE DE RETOUR (Celle qui manquait !) [FIX]
    // =================================================
    @Bean
    public Queue processedQueue() {
        // C'est la file que ProcessedConsumer écoute. Elle doit exister !
        return new Queue("processed-queue", true);
    }
}