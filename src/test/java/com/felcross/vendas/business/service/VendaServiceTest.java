package com.felcross.vendas.business.service;

import com.felcross.vendas.api.dto.ItemVendaRequest;
import com.felcross.vendas.api.dto.VendaRequest;
import com.felcross.vendas.api.dto.VendaResponse;
import com.felcross.vendas.business.mapper.VendaMapper;
import com.felcross.vendas.domain.entity.StatusVenda;
import com.felcross.vendas.domain.entity.Venda;
import com.felcross.vendas.domain.repository.DescontoStrategy;
import com.felcross.vendas.domain.repository.VendaRepository;
import com.felcross.vendas.infrastructure.exception.BusinessException;
import com.felcross.vendas.infrastructure.exception.ResourceNotFoundException;
import com.felcross.vendas.infrastructure.feign.ClienteClient;
import com.felcross.vendas.infrastructure.feign.ProdutoClient;
import com.felcross.vendas.infrastructure.feign.dto.ClienteDto;
import com.felcross.vendas.infrastructure.feign.dto.ProdutoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository repository;
    @Mock
    private ClienteClient clienteClient;
    @Mock
    private ProdutoClient produtoClient;
    @Mock
    private NotificacaoProducer notificacaoProducer;
    @Mock
    private VendaMapper mapper;

    @InjectMocks
    private VendaService vendaService;

    @Test
    @DisplayName("Deve criar uma venda com sucesso")
    void deveCriarVendaComSucesso() {
        VendaRequest request = VendaRequest.builder()
                .clienteId(1L)
                .itens(List.of(new ItemVendaRequest("p1", 2)))
                .build();

        when(clienteClient.buscar(1L)).thenReturn(new ClienteDto(1L, "Fel", "e@e.com"));
        when(produtoClient.buscar("p1")).thenReturn(new ProdutoDto("p1", "Produto", new BigDecimal("10"), 10));
        when(repository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponse(any())).thenReturn(new VendaResponse());

        VendaResponse res = vendaService.criar(request);

        assertNotNull(res);
        verify(repository).save(any());
        verify(notificacaoProducer).dispararEventoEmail(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException por estoque insuficiente")
    void erroEstoque() {
        VendaRequest request = VendaRequest.builder()
                .clienteId(1L)
                .itens(List.of(new ItemVendaRequest("p1", 100)))
                .build();

        when(clienteClient.buscar(1L)).thenReturn(new ClienteDto(1L, "Fel", "e@e.com"));
        when(produtoClient.buscar("p1")).thenReturn(new ProdutoDto("p1", "Prod", BigDecimal.TEN, 5));

        assertThrows(BusinessException.class, () -> vendaService.criar(request));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException para venda inexistente")
    void erroBusca() {
        when(repository.findById("invalido")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vendaService.buscarPorId("invalido"));
    }

    @Test
    @DisplayName("Deve confirmar venda com sucesso")
    void confirmarVenda() {
        Venda v = Venda.builder().status(StatusVenda.PENDENTE).build();
        when(repository.findById("1")).thenReturn(Optional.of(v));
        when(repository.save(any())).thenReturn(v);
        when(mapper.toResponse(any())).thenReturn(new VendaResponse());

        VendaResponse res = vendaService.confirmar("1");

        assertEquals(StatusVenda.CONFIRMADA, v.getStatus());
        assertNotNull(res);
    }
}