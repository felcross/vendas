package com.felcross.vendas.infrastructure.feign.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                String auth = servletAttrs.getRequest().getHeader("Authorization");
                if (auth != null) {
                    // ADICIONE ESTA LINHA:
                    System.out.println("DEBUG: Repassando Token para o Feign -> " + auth.substring(0, 15) + "...");
                    template.header("Authorization", auth);
                } else {
                    System.out.println("DEBUG: Erro! Header Authorization não encontrado na requisição de entrada.");
                }
            } else {
                System.out.println("DEBUG: Erro! Contexto da requisição é nulo (Provável problema de Thread).");
            }
        };
    }
}
