package com.felcross.vendas.business.service;

import com.felcross.vendas.api.dto.*;
import com.felcross.vendas.business.mapper.VendaMapper;
import com.felcross.vendas.domain.entity.*;
import com.felcross.vendas.domain.repository.DescontoStrategy;
import com.felcross.vendas.domain.repository.VendaRepository;
import com.felcross.vendas.infrastructure.exception.BusinessException;
import com.felcross.vendas.infrastructure.exception.ResourceNotFoundException;
import com.felcross.vendas.infrastructure.feign.*;
import com.felcross.vendas.infrastructure.feign.dto.ClienteDto;
import com.felcross.vendas.infrastructure.feign.dto.ProdutoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository repository;
    private final ClienteClient clienteClient;
    private final ProdutoClient produtoClient;
    private final NotificacaoProducer notificacaoProducer;
    private final VendaMapper mapper; // Removi a lista de descontos daqui

    public VendaResponse criar(VendaRequest req) {
        ClienteDto cliente = clienteClient.buscar(req.getClienteId());

        List<ItemVenda> itens = req.getItens().stream().map(itemReq -> {
            ProdutoDto produto = produtoClient.buscar(itemReq.getProdutoId());

            if (produto.getEstoque() < itemReq.getQuantidade())
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());

            BigDecimal subtotalItem = produto.getPreco()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantidade()));

            return ItemVenda.builder()
                    .produtoId(produto.getId())
                    .nomeProduto(produto.getNome())
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .subtotal(subtotalItem)
                    .build();
        }).toList();

        BigDecimal subtotal = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Venda venda = Venda.builder()
                .clienteId(cliente.getId())
                .clienteEmail(cliente.getEmail())
                .itens(itens)
                .subtotal(subtotal)
                .desconto(BigDecimal.ZERO)
                .total(subtotal)
                .status(StatusVenda.PENDENTE)
                .createdAt(LocalDateTime.now())
                .build();

        // LOGICA DE DESCONTO REMOVIDA DAQUI

        Venda salva = repository.save(venda);

        req.getItens().forEach(item ->
                produtoClient.decrementarEstoque(item.getProdutoId(), item.getQuantidade()));

        dispararNotificacaoVenda(salva, cliente);

        return toResponse(salva);
    }

    public VendaResponse buscarPorId(String id) {
        return toResponse(findOrThrow(id));
    }

    public List<VendaResponse> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId)
                .stream().map(this::toResponse).toList();
    }

    public VendaResponse confirmar(String id) {
        Venda v = findOrThrow(id);
        if (v.getStatus() != StatusVenda.PENDENTE)
            throw new BusinessException("Apenas vendas PENDENTES podem ser confirmadas");
        v.setStatus(StatusVenda.CONFIRMADA);
        return toResponse(repository.save(v));
    }

    public VendaResponse cancelar(String id) {
        Venda v = findOrThrow(id);
        if (v.getStatus() == StatusVenda.CANCELADA)
            throw new BusinessException("Venda ja esta cancelada");
        v.setStatus(StatusVenda.CANCELADA);
        return toResponse(repository.save(v));
    }

    private Venda findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda nao encontrada: " + id));
    }

    private VendaResponse toResponse(Venda v) {
        return mapper.toResponse(v);
    }

    private void dispararNotificacaoVenda(Venda venda, ClienteDto cliente) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("valorTotal", venda.getTotal());
        variaveis.put("idVenda", venda.getId());
        variaveis.put("dataVenda", venda.getCreatedAt().toString());

        NotificacaoEmailRecord notificacao = new NotificacaoEmailRecord(
                venda.getClienteEmail(),
                "Confirmação de Pedido #" + venda.getId(),
                cliente.getNome(),
                "Sua compra foi realizada com sucesso!",
                variaveis,
                "confirmacao-venda"
        );
        notificacaoProducer.dispararEventoEmail(notificacao);
    }
}