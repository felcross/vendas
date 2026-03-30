package com.felcross.vendas.api.dto;

import com.felcross.vendas.domain.entity.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaResponse {
    private String id;
    private Long clienteId;
    private String clienteEmail;
    private List<ItemVenda> itens;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal total;
    private StatusVenda status;
    private LocalDateTime createdAt;
}