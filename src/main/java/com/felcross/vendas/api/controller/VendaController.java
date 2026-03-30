package com.felcross.vendas.api.controller;

import com.felcross.vendas.api.dto.*;
import com.felcross.vendas.business.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas")
@SecurityRequirement(name = "bearerAuth")
public class VendaController {

    private final VendaService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar venda")
    public VendaResponse criar(@Valid @RequestBody VendaRequest req) {
        return service.criar(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID")
    public VendaResponse buscar(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar vendas por cliente")
    public List<VendaResponse> listarPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar venda")
    public VendaResponse confirmar(@PathVariable String id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar venda")
    public VendaResponse cancelar(@PathVariable String id) {
        return service.cancelar(id);
    }
}