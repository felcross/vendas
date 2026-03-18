package com.felcross.vendas.infrastructure.feign;

import com.felcross.vendas.api.dto.ProdutoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "produtos", url = "${produtos.url}")
public interface ProdutoClient {
    @GetMapping("/produtos/{id}")
    ProdutoDto buscar(@PathVariable Long id);

    @PatchMapping("/produtos/{id}/estoque/{quantidade}")
    void decrementarEstoque(@PathVariable Long id, @PathVariable int quantidade);
}
