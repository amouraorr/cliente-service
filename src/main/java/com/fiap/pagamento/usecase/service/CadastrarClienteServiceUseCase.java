package com.fiap.pagamento.usecase.service;

import com.fiap.pagamento.domain.Cliente;
import com.fiap.pagamento.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastrarClienteServiceUseCase {

    private final ClienteGateway gateway;

    public Cliente execute(Cliente cliente) {
        gateway.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            throw new IllegalArgumentException("CPF já cadastrado!");
        });
        return gateway.salvar(cliente);
    }
}
