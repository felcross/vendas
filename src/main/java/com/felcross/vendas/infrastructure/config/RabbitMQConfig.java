package com.felcross.vendas.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NOTIFICACAO = "ex.notificacao";
    public static final String QUEUE_EMAIL = "q.enviar-email";
    public static final String ROUTING_KEY = "notificacao.email";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_EMAIL, true); // durable: true para não perder a fila se o Rabbit cair
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NOTIFICACAO);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // O "pulo do gato": Transforma o Record em JSON automaticamente na fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}