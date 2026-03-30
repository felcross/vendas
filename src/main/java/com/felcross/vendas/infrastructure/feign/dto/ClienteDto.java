package com.felcross.vendas.infrastructure.feign.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDto {
    private Long id;
    private String nome;
    private String email;
}
