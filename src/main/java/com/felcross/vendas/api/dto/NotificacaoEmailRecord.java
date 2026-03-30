package com.felcross.vendas.api.dto;

import java.util.Map;

public record NotificacaoEmailRecord(
        String destinatario,
        String assunto,
        String nomeUsuario,
        String mensagemCorpo,
        Map<String, Object> variaveisTemplate,
        String templateName // Ex: "notificacao-tarefa" ou "confirmacao-venda"
) {}