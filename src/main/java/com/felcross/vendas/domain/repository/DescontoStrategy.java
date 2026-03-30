package com.felcross.vendas.domain.repository;

import com.felcross.vendas.domain.entity.Venda;

import java.math.BigDecimal;

public interface DescontoStrategy {
    // Calcula o valor do desconto baseado na venda
    BigDecimal calcular(Venda venda);
    // Define se essa estratégia se aplica à venda atual
    boolean aplicavel(Venda venda);
}