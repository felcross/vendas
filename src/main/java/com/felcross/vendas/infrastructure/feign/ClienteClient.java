package com.felcross.vendas.infrastructure.feign;

import com.felcross.vendas.infrastructure.feign.config.FeignConfig;
import com.felcross.vendas.infrastructure.feign.dto.ClienteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes", url = "${clientes.url}",configuration = FeignConfig.class)
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteDto buscar(@PathVariable Long id);
}