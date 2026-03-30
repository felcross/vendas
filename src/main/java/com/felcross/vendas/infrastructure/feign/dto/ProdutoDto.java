package com.felcross.vendas.infrastructure.feign.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdutoDto {
    private String id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;
}
