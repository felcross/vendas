package com.felcross.vendas.business.service;

import com.felcross.vendas.domain.entity.Venda;
import com.felcross.vendas.domain.repository.DescontoStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DescontoSemDesconto implements DescontoStrategy {

    @Override
    public BigDecimal calcular(Venda venda) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean aplicavel(Venda venda) {
        // Fallback — sempre aplicável quando nenhuma outra estratégia se aplica
        return true;
    }
}