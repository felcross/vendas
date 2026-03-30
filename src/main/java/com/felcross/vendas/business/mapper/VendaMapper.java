package com.felcross.vendas.business.mapper;

import com.felcross.vendas.api.dto.VendaResponse;
import com.felcross.vendas.domain.entity.Venda;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendaMapper {


    VendaResponse toResponse(Venda venda);
}
