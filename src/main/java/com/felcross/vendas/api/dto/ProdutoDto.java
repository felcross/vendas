package com.felcross.vendas.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoDto {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;
}
