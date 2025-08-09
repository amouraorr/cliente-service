package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.domain.Endereco;
import com.fiap.cliente.gateway.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceUseCase {

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

        existente.setNome(cliente.getNome());
        existente.setDataNascimento(cliente.getDataNascimento());

        // Atualiza o endereço completo
        if (cliente.getEndereco() != null) {
            Endereco enderecoExistente = existente.getEndereco();
            Endereco novoEndereco = cliente.getEndereco();

            if (enderecoExistente == null) {
                existente.setEndereco(novoEndereco);
            } else {
                enderecoExistente.setRua(novoEndereco.getRua());
                enderecoExistente.setNumero(novoEndereco.getNumero());
                enderecoExistente.setCep(novoEndereco.getCep());
                enderecoExistente.setCidade(novoEndereco.getCidade());
                enderecoExistente.setEstado(novoEndereco.getEstado());
            }
        }

        return gateway.atualizar(existente);
    }
}