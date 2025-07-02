package com.fiap.cliente.controller;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.dto.ClienteDTO;
import com.fiap.cliente.mapper.ClienteMapper;
import com.fiap.cliente.usecase.AtualizarClienteUseCase;
import com.fiap.cliente.usecase.BuscarClientePorCpfUseCase;
import com.fiap.cliente.usecase.CadastrarClienteUseCase;
import com.fiap.cliente.usecase.ListarClientesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarUseCase;
    private final AtualizarClienteUseCase atualizarUseCase;
    private final BuscarClientePorCpfUseCase buscarPorCpfUseCase;
    private final ListarClientesUseCase listarUseCase;
    private final ClienteMapper mapper;

    @PostMapping
    public ResponseEntity<ClienteDTO> cadastrar(@RequestBody ClienteDTO dto) {
        Cliente cliente = mapper.toDomain(dto);
        Cliente salvo = cadastrarUseCase.execute(cliente);
        return ResponseEntity.ok(mapper.toDTO(salvo));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDTO> buscarPorCpf(@PathVariable String cpf) {
        return buscarPorCpfUseCase.execute(cpf)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClienteDTO> listarTodos() {
        return listarUseCase.execute().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        Cliente cliente = mapper.toDomain(dto);
        Cliente atualizado = atualizarUseCase.execute(id, cliente);
        return ResponseEntity.ok(mapper.toDTO(atualizado));
    }
}
