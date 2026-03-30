package com.felcross.vendas.business.service;

import com.felcross.vendas.api.dto.NotificacaoEmailRecord;
import com.felcross.vendas.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void dispararEventoEmail(NotificacaoEmailRecord dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NOTIFICACAO,
                RabbitMQConfig.ROUTING_KEY,
                dto
        );
    }
}