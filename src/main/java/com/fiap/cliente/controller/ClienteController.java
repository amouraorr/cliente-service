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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

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
        // Recebe ClienteRequestDTO, converte para domínio, executa cadastro e retorna ResponseDTO
        Cliente cliente = mapper.toDomain(dto);
        Cliente salvo = cadastrarUseCase.execute(cliente);
        return ResponseEntity.ok(mapper.toResponseDTO(salvo));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        // Busca cliente por CPF e retorna ResponseDTO ou 404
        return buscarPorCpfUseCase.execute(cpf)
                .map(mapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClienteResponseDTO> listarTodos() {
        // Lista todos os clientes
        return listarUseCase.execute().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        // Atualiza cliente pelo ID usando ClienteRequestDTO
        Cliente cliente = mapper.toDomain(dto);
        Cliente atualizado = atualizarUseCase.execute(id, cliente);
        return ResponseEntity.ok(mapper.toResponseDTO(atualizado));
    }
}