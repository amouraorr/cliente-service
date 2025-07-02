package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarClientePorCpfUseCase {

    private final ClienteGateway gateway;

    public Optional<Cliente> execute(String cpf) {
        return gateway.buscarPorCpf(cpf);
    }
}
