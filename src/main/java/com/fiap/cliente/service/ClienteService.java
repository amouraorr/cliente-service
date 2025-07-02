package com.fiap.cliente.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteGateway gateway;

    public Cliente cadastrar(Cliente cliente) {
        gateway.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            throw new IllegalArgumentException("CPF já cadastrado!");
        });
        return gateway.salvar(cliente);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return gateway.buscarPorCpf(cpf);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return gateway.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return gateway.listarTodos();
    }

    public Cliente atualizar(Cliente cliente) {
        Cliente existente = gateway.buscarPorId(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado!"));
        // Atualize os campos necessários
        existente.setNome(cliente.getNome());
        existente.setDataNascimento(cliente.getDataNascimento());
        existente.setRua(cliente.getRua());
        existente.setNumero(cliente.getNumero());
        existente.setCep(cliente.getCep());
        return gateway.atualizar(existente);
    }
}