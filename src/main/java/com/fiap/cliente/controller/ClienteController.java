package com.fiap.cliente.controller;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.dto.request.ClienteRequestDTO;
import com.fiap.cliente.dto.response.ClienteResponseDTO;
import com.fiap.cliente.mapper.ClienteMapper;
import com.fiap.cliente.usecase.service.AtualizarClienteServiceUseCase;
import com.fiap.cliente.usecase.service.BuscarClientePorCpfServiceUseCase;
import com.fiap.cliente.usecase.service.CadastrarClienteServiceUseCase;
import com.fiap.cliente.usecase.service.ListarClientesServiceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CadastrarClienteServiceUseCase cadastrarUseCase;
    private final AtualizarClienteServiceUseCase atualizarUseCase;
    private final BuscarClientePorCpfServiceUseCase buscarPorCpfUseCase;
    private final ListarClientesServiceUseCase listarUseCase;
    private final ClienteMapper mapper;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody ClienteRequestDTO dto) {
        log.info("Iniciando cadastro de cliente com CPF: {}", dto.getCpf());
        Cliente cliente = mapper.toDomain(dto);
        Cliente salvo = cadastrarUseCase.execute(cliente);
        log.info("Cliente cadastrado com sucesso, ID: {}", salvo.getId());
        return ResponseEntity.ok(mapper.toResponseDTO(salvo));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        log.info("Buscando cliente por CPF: {}", cpf);
        return buscarPorCpfUseCase.execute(cpf)
                .map(cliente -> {
                    log.info("Cliente encontrado para CPF: {}", cpf);
                    return ResponseEntity.ok(mapper.toResponseDTO(cliente));
                })
                .orElseGet(() -> {
                    log.warn("Cliente não encontrado para CPF: {}", cpf);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public List<ClienteResponseDTO> listarTodos() {
        log.info("Listando todos os clientes");
        List<ClienteResponseDTO> clientes = listarUseCase.execute().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("Total de clientes listados: {}", clientes.size());
        return clientes;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        log.info("Atualizando cliente ID: {}", id);
        Cliente cliente = mapper.toDomain(dto);
        Cliente atualizado = atualizarUseCase.execute(id, cliente);
        log.info("Cliente atualizado com sucesso, ID: {}", atualizado.getId());
        return ResponseEntity.ok(mapper.toResponseDTO(atualizado));
    }
}