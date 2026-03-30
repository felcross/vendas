package com.felcross.vendas.infrastructure.feign;

import com.felcross.vendas.infrastructure.feign.config.FeignConfig;
import com.felcross.vendas.infrastructure.feign.dto.ProdutoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "produtos", url = "${produtos.url}",configuration = FeignConfig.class)
public interface ProdutoClient {

    @GetMapping("/produtos/{id}")
    ProdutoDto buscar(@PathVariable String id);

    @PatchMapping("/produtos/{id}/estoque/{quantidade}")
    void decrementarEstoque(@PathVariable String id, @PathVariable int quantidade);
}
