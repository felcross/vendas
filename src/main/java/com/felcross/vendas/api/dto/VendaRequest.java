package com.felcross.vendas.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class VendaRequest {
    @NotNull private Long clienteId;
    @NotEmpty private List<ItemVendaRequest> itens;
}
