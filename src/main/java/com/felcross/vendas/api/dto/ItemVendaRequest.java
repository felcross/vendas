package com.felcross.vendas.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemVendaRequest {
    @NotNull private Long produtoId;
    @NotNull @Min(1) private Integer quantidade;
}
