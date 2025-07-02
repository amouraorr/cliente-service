package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastrarClienteUseCase {

    private final ClienteGateway gateway;

    public Cliente execute(Cliente cliente) {
        gateway.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            throw new IllegalArgumentException("CPF já cadastrado!");
        });
        return gateway.salvar(cliente);
    }
}
