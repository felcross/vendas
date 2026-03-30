package com.felcross.vendas.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaRequest {
    @NotNull
    private Long clienteId;
    @NotEmpty
    private List<ItemVendaRequest> itens;
}