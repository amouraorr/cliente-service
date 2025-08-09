package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarClientesServiceUseCase {

    private final ClienteGateway gateway;

    public List<Cliente> execute() {
        return gateway.listarTodos();
    }
}
