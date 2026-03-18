package com.felcross.vendas.business.service;

import com.felcross.vendas.api.dto.*;
import com.felcross.vendas.domain.entity.*;
import com.felcross.vendas.domain.repository.VendaRepository;
import com.felcross.vendas.infrastructure.feign.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {
    private final VendaRepository repository;
    private final ClienteClient clienteClient;
    private final ProdutoClient produtoClient;
    private final List<DescontoStrategy> descontoStrategies;

    public VendaResponse criar(VendaRequest req) {
        // Valida cliente
        ClienteDto cliente = clienteClient.buscar(req.getClienteId());

        // Monta itens e valida estoque
        List<ItemVenda> itens = req.getItens().stream().map(item -> {
            ProdutoDto produto = produtoClient.buscar(item.getProdutoId());
            if (produto.getEstoque() < item.getQuantidade())
                throw new IllegalArgumentException("Estoque insuficiente: " + produto.getNome());
            BigDecimal subtotal = produto.getPreco()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));
            return ItemVenda.builder()
                .produtoId(produto.getId()).nomeProduto(produto.getNome())
                .quantidade(item.getQuantidade()).precoUnitario(produto.getPreco())
                .subtotal(subtotal).build();
        }).toList();

        BigDecimal subtotal = itens.stream()
            .map(ItemVenda::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Venda venda = Venda.builder()
            .clienteId(cliente.getId()).itens(itens).subtotal(subtotal)
            .desconto(BigDecimal.ZERO).total(subtotal)
            .status(StatusVenda.PENDENTE).createdAt(LocalDateTime.now()).build();

        // Aplica strategy de desconto
        DescontoStrategy strategy = descontoStrategies.stream()
            .filter(s -> s.aplicavel(venda) && !(s instanceof DescontoSemDesconto))
            .findFirst().orElse(new DescontoSemDesconto());

        BigDecimal desconto = strategy.calcular(venda);
        venda.setDesconto(desconto);
        venda.setTotal(subtotal.subtract(desconto));

        Venda salva = repository.save(venda);

        // Decrementa estoque após persistir
        req.getItens().forEach(item ->
            produtoClient.decrementarEstoque(item.getProdutoId(), item.getQuantidade()));

        return toResponse(salva);
    }

    public VendaResponse buscarPorId(String id) { return toResponse(findOrThrow(id)); }

    public List<VendaResponse> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream().map(this::toResponse).toList();
    }

    public VendaResponse confirmar(String id) {
        Venda v = findOrThrow(id);
        if (v.getStatus() != StatusVenda.PENDENTE)
            throw new IllegalStateException("Venda nao esta pendente");
        v.setStatus(StatusVenda.CONFIRMADA);
        return toResponse(repository.save(v));
    }

    public VendaResponse cancelar(String id) {
        Venda v = findOrThrow(id);
        if (v.getStatus() == StatusVenda.CANCELADA)
            throw new IllegalStateException("Venda ja cancelada");
        v.setStatus(StatusVenda.CANCELADA);
        return toResponse(repository.save(v));
    }

    private Venda findOrThrow(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Venda nao encontrada: " + id));
    }

    private VendaResponse toResponse(Venda v) {
        return VendaResponse.builder().id(v.getId()).clienteId(v.getClienteId())
            .itens(v.getItens()).subtotal(v.getSubtotal()).desconto(v.getDesconto())
            .total(v.getTotal()).status(v.getStatus()).createdAt(v.getCreatedAt()).build();
    }
}
