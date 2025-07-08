package com.fiap.pagamento.usecase.service;

import com.fiap.pagamento.domain.Cliente;
import com.fiap.pagamento.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarClienteServiceUseCase {

    private final ClienteGateway gateway;

    public Cliente execute(Long id, Cliente cliente) {
        cliente.setId(id);
        return gateway.atualizar(cliente);
    }
}
