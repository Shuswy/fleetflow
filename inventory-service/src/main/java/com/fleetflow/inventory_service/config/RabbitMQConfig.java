package com.fleetflow.inventory_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String INCOMING_QUEUE = "inventory_stock_check_queue";

    @Bean
    public Queue incomingQueue() {
        return new Queue(INCOMING_QUEUE);
    }

    public static final String REPLY_EXCHANGE = "inventory_events_exchange";
    public static final String REPLY_ROUTING_KEY = "stock.updated";

    public static final String ORDER_UPDATE_QUEUE = "order_status_update_queue";

    @Bean
    public TopicExchange replyExchange() {
        return new TopicExchange(REPLY_EXCHANGE);
    }

    @Bean
    public Queue orderUpdateQueue() {
        return new Queue(ORDER_UPDATE_QUEUE);
    }

    @Bean
    public Binding replyBinding() {
        return BindingBuilder
                .bind(orderUpdateQueue())
                .to(replyExchange())
                .with(REPLY_ROUTING_KEY);
    }
}
