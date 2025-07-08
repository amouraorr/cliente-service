package com.fiap.pagamento.usecase.service;

import com.fiap.pagamento.domain.Cliente;
import com.fiap.pagamento.gateway.ClienteGateway;
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
