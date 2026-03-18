package com.felcross.vendas.business.service;

import com.felcross.vendas.domain.entity.Venda;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DescontoAcimaDe500 implements DescontoStrategy {
    private static final BigDecimal MINIMO = new BigDecimal("500");
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.05");

    @Override
    public BigDecimal calcular(Venda venda) {
        return venda.getSubtotal().multiply(PERCENTUAL);
    }

    @Override
    public boolean aplicavel(Venda venda) {
        return venda.getSubtotal().compareTo(MINIMO) >= 0;
    }
}
