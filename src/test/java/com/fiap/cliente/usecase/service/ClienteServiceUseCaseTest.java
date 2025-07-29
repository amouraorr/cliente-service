package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.domain.Endereco;
import com.fiap.cliente.gateway.ClienteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ClienteServiceUseCase service;

    private Cliente clienteParaCadastro;
    private Cliente clienteExistente;
    private Cliente clienteSalvo;
    private Endereco endereco;
    private Endereco novoEndereco;

    @BeforeEach
    void setUp() {
        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cep("01234-567")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        novoEndereco = Endereco.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cep("01310-100")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        clienteParaCadastro = Cliente.builder()
                .cpf("12345678901")
                .nome("João Silva")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();

        clienteSalvo = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();

        clienteExistente = Cliente.builder()
                .id(2L)
                .cpf("98765432100")
                .nome("Maria Santos")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(endereco)
                .build();
    }

    // TESTES PARA CADASTRAR
    @Test
    void deveCadastrarClienteComSucessoQuandoCpfNaoExiste() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteParaCadastro))
                .thenReturn(clienteSalvo);

        // When
        Cliente resultado = service.cadastrar(clienteParaCadastro);

        // Then
        assertNotNull(resultado);
        assertEquals(clienteSalvo.getId(), resultado.getId());
        assertEquals(clienteSalvo.getCpf(), resultado.getCpf());
        assertEquals(clienteSalvo.getNome(), resultado.getNome());

        verify(gateway).buscarPorCpf(clienteParaCadastro.getCpf());
        verify(gateway).salvar(clienteParaCadastro);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveLancarExcecaoAoCadastrarClienteComCpfJaExistente() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(clienteParaCadastro)
        );

        assertEquals("CPF já cadastrado!", exception.getMessage());

        verify(gateway).buscarPorCpf(clienteParaCadastro.getCpf());
        verify(gateway, never()).salvar(any(Cliente.class));
        verifyNoMoreInteractions(gateway);
    }

    // TESTES PARA BUSCAR POR CPF
    @Test
    void deveRetornarClienteAoBuscarPorCpfExistente() {
        // Given
        String cpf = "12345678901";
        when(gateway.buscarPorCpf(cpf))
                .thenReturn(Optional.of(clienteSalvo));

        // When
        Optional<Cliente> resultado = service.buscarPorCpf(cpf);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(clienteSalvo.getCpf(), resultado.get().getCpf());
        assertEquals(clienteSalvo.getNome(), resultado.get().getNome());

        verify(gateway).buscarPorCpf(cpf);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarVazioAoBuscarPorCpfInexistente() {
        // Given
        String cpf = "99999999999";
        when(gateway.buscarPorCpf(cpf))
                .thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = service.buscarPorCpf(cpf);

        // Then
        assertFalse(resultado.isPresent());

        verify(gateway).buscarPorCpf(cpf);
        verifyNoMoreInteractions(gateway);
    }

    // TESTES PARA BUSCAR POR ID
    @Test
    void deveRetornarClienteAoBuscarPorIdExistente() {
        // Given
        Long id = 1L;
        when(gateway.buscarPorId(id))
                .thenReturn(Optional.of(clienteSalvo));

        // When
        Optional<Cliente> resultado = service.buscarPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(clienteSalvo.getId(), resultado.get().getId());
        assertEquals(clienteSalvo.getNome(), resultado.get().getNome());

        verify(gateway).buscarPorId(id);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarVazioAoBuscarPorIdInexistente() {
        // Given
        Long id = 999L;
        when(gateway.buscarPorId(id))
                .thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = service.buscarPorId(id);

        // Then
        assertFalse(resultado.isPresent());

        verify(gateway).buscarPorId(id);
        verifyNoMoreInteractions(gateway);
    }

    // TESTES PARA LISTAR TODOS
    @Test
    void deveRetornarListaDeClientesAoListarTodos() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteSalvo, clienteExistente);
        when(gateway.listarTodos())
                .thenReturn(clientes);

        // When
        List<Cliente> resultado = service.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(clienteSalvo.getId(), resultado.get(0).getId());
        assertEquals(clienteExistente.getId(), resultado.get(1).getId());

        verify(gateway).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        // Given
        when(gateway.listarTodos())
                .thenReturn(Collections.emptyList());

        // When
        List<Cliente> resultado = service.listarTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(gateway).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    // TESTES PARA ATUALIZAR
    @Test
    void deveAtualizarClienteComSucesso() {
        // Given
        Cliente clienteParaAtualizar = Cliente.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(novoEndereco)
                .build();

        Cliente clienteAtualizado = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva Atualizado")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(novoEndereco)
                .build();

        when(gateway.buscarPorId(1L))
                .thenReturn(Optional.of(clienteSalvo));
        when(gateway.atualizar(any(Cliente.class)))
                .thenReturn(clienteAtualizado);

        // When
        Cliente resultado = service.atualizar(clienteParaAtualizar);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());
        assertEquals("Avenida Paulista", resultado.getEndereco().getRua());

        verify(gateway).buscarPorId(1L);
        verify(gateway).atualizar(any(Cliente.class));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveAtualizarClienteSemEnderecoExistente() {
        // Given
        Cliente clienteSemEndereco = Cliente.builder()
                .id(3L)
                .cpf("11111111111")
                .nome("Pedro Santos")
                .dataNascimento(LocalDate.of(1995, 1, 1))
                .build();

        Cliente clienteParaAtualizar = Cliente.builder()
                .id(3L)
                .nome("Pedro Santos Atualizado")
                .dataNascimento(LocalDate.of(1995, 1, 1))
                .endereco(novoEndereco)
                .build();

        when(gateway.buscarPorId(3L))
                .thenReturn(Optional.of(clienteSemEndereco));
        when(gateway.atualizar(any(Cliente.class)))
                .thenReturn(clienteParaAtualizar);

        // When
        Cliente resultado = service.atualizar(clienteParaAtualizar);

        // Then
        assertNotNull(resultado);
        assertEquals("Pedro Santos Atualizado", resultado.getNome());
        assertNotNull(resultado.getEndereco());
        assertEquals("Avenida Paulista", resultado.getEndereco().getRua());

        verify(gateway).buscarPorId(3L);
        verify(gateway).atualizar(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteComEnderecoNulo() {
        // Given
        Cliente clienteParaAtualizar = Cliente.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(null)
                .build();

        when(gateway.buscarPorId(1L))
                .thenReturn(Optional.of(clienteSalvo));
        when(gateway.atualizar(any(Cliente.class)))
                .thenReturn(clienteParaAtualizar);

        // When
        Cliente resultado = service.atualizar(clienteParaAtualizar);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());

        verify(gateway).buscarPorId(1L);
        verify(gateway).atualizar(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Given
        Cliente clienteParaAtualizar = Cliente.builder()
                .id(999L)
                .nome("Cliente Inexistente")
                .build();

        when(gateway.buscarPorId(999L))
                .thenReturn(Optional.empty());

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(clienteParaAtualizar)
        );

        assertEquals("Cliente não encontrado!", exception.getMessage());

        verify(gateway).buscarPorId(999L);
        verify(gateway, never()).atualizar(any(Cliente.class));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveAtualizarEnderecoExistenteComNovosDados() {
        // Given
        Endereco enderecoAntigo = Endereco.builder()
                .rua("Rua Antiga")
                .numero("100")
                .cep("00000-000")
                .cidade("Cidade Antiga")
                .estado("AA")
                .build();

        Cliente clienteComEnderecoAntigo = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Silva")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(enderecoAntigo)
                .build();

        Cliente clienteParaAtualizar = Cliente.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(novoEndereco)
                .build();

        when(gateway.buscarPorId(1L))
                .thenReturn(Optional.of(clienteComEnderecoAntigo));
        when(gateway.atualizar(any(Cliente.class)))
                .thenReturn(clienteParaAtualizar);

        // When
        Cliente resultado = service.atualizar(clienteParaAtualizar);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());
        assertEquals("Avenida Paulista", resultado.getEndereco().getRua());
        assertEquals("456", resultado.getEndereco().getNumero());
        assertEquals("01310-100", resultado.getEndereco().getCep());

        verify(gateway).buscarPorId(1L);
        verify(gateway).atualizar(any(Cliente.class));
    }
}