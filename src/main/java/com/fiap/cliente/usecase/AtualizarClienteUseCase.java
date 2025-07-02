package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarClienteUseCase {

    private final ClienteGateway gateway;

    public Cliente execute(Long id, Cliente cliente) {
        cliente.setId(id);
        return gateway.atualizar(cliente);
    }
}
