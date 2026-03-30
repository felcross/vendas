package com.felcross.vendas.business.mapper;

import com.felcross.vendas.api.dto.VendaResponse;
import com.felcross.vendas.domain.entity.ItemVenda;
import com.felcross.vendas.domain.entity.Venda;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T18:09:50-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.0.0.jar, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class VendaMapperImpl implements VendaMapper {

    @Override
    public VendaResponse toResponse(Venda venda) {
        if ( venda == null ) {
            return null;
        }

        VendaResponse.VendaResponseBuilder vendaResponse = VendaResponse.builder();

        vendaResponse.id( venda.getId() );
        vendaResponse.clienteId( venda.getClienteId() );
        vendaResponse.clienteEmail( venda.getClienteEmail() );
        List<ItemVenda> list = venda.getItens();
        if ( list != null ) {
            vendaResponse.itens( new ArrayList<ItemVenda>( list ) );
        }
        vendaResponse.subtotal( venda.getSubtotal() );
        vendaResponse.desconto( venda.getDesconto() );
        vendaResponse.total( venda.getTotal() );
        vendaResponse.status( venda.getStatus() );
        vendaResponse.createdAt( venda.getCreatedAt() );

        return vendaResponse.build();
    }
}
