package com.felcross.vendas.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "vendas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Venda {
    @Id
    private String id;
    private Long clienteId;
    private List<ItemVenda> itens;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal total;
    private StatusVenda status;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
