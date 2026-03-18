package com.felcross.vendas.domain.repository;

import com.felcross.vendas.domain.entity.Venda;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface VendaRepository extends MongoRepository<Venda, String> {
    List<Venda> findByClienteId(Long clienteId);
}
