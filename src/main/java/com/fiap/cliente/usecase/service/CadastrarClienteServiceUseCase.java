package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CadastrarClienteServiceUseCase {

    private final ClienteGateway gateway;

    public Cliente execute(Cliente cliente) {
        log.info("Verifica existência de CPF: {}", cliente.getCpf());
        gateway.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            log.warn("CPF já cadastrado: {}", cliente.getCpf());
            throw new IllegalArgumentException("CPF já cadastrado!");
        });
        Cliente salvo = gateway.salvar(cliente);
        log.info("Cliente salvo com ID: {}", salvo.getId());
        return salvo;
    }
}
