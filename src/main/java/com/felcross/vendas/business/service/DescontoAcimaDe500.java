package com.felcross.vendas.business.service;

import com.felcross.vendas.domain.entity.Venda;
import com.felcross.vendas.domain.repository.DescontoStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DescontoAcimaDe500 implements DescontoStrategy {

    private static final BigDecimal MINIMO = new BigDecimal("500");
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.05");

    @Override
    public BigDecimal calcular(Venda venda) {
        // 5% de desconto no subtotal
        return venda.getSubtotal().multiply(PERCENTUAL);
    }

    @Override
    public boolean aplicavel(Venda venda) {
        // Só aplica se o subtotal for maior ou igual a R$500
        return venda.getSubtotal().compareTo(MINIMO) >= 0;
    }
}