package com.felcross.vendas.business.service;

import com.felcross.vendas.domain.entity.Venda;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DescontoSemDesconto implements DescontoStrategy {
    @Override
    public BigDecimal calcular(Venda venda) { return BigDecimal.ZERO; }

    @Override
    public boolean aplicavel(Venda venda) { return true; }
}
