package com.fiap.pagamento.gateway;

import com.fiap.pagamento.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteGateway {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorCpf(String cpf);
    Optional<Cliente> buscarPorId(Long id);
    List<Cliente> listarTodos();
    Cliente atualizar(Cliente cliente);
}


