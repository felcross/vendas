package com.felcross.vendas.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemVendaRequest {
    @NotBlank
    private String produtoId;
    @NotNull @Min(1)
    private Integer quantidade;
}
