package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarClientePorCpfServiceUseCase {

    private final ClienteGateway gateway;

    public Optional<Cliente> execute(String cpf) {
        return gateway.buscarPorCpf(cpf);
    }
}
