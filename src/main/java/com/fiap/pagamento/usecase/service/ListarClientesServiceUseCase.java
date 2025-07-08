package com.fiap.pagamento.usecase.service;

import com.fiap.pagamento.domain.Cliente;
import com.fiap.pagamento.gateway.ClienteGateway;
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
