package com.felcross.vendas.business.service;

import com.felcross.vendas.domain.entity.Venda;
import java.math.BigDecimal;

public interface DescontoStrategy {
    BigDecimal calcular(Venda venda);
    boolean aplicavel(Venda venda);
}
