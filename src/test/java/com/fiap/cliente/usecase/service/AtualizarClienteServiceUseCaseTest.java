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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarClienteServiceUseCaseTest {

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private AtualizarClienteServiceUseCase atualizarClienteServiceUseCase;

    private Cliente clienteInput;
    private Cliente clienteRetornado;
    private Long idCliente;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        idCliente = 1L;

        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cep("01234-567")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        clienteInput = Cliente.builder()
                .nome("João da Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();

        clienteRetornado = Cliente.builder()
                .id(idCliente)
                .nome("João da Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        // Arrange
        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornado);

        // Act
        Cliente resultado = atualizarClienteServiceUseCase.execute(idCliente, clienteInput);

        // Assert
        assertNotNull(resultado);
        assertEquals(idCliente, resultado.getId());
        assertEquals(clienteInput.getNome(), resultado.getNome());
        assertEquals(clienteInput.getCpf(), resultado.getCpf());
        assertEquals(clienteInput.getDataNascimento(), resultado.getDataNascimento());
        assertEquals(clienteInput.getEndereco(), resultado.getEndereco());

        assertEquals(idCliente, clienteInput.getId());

        verify(clienteGateway, times(1)).atualizar(clienteInput);
    }

    @Test
    void deveDefinirIdNoClienteAntesDePassarParaGateway() {
        // Arrange
        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornado);

        // Act
        atualizarClienteServiceUseCase.execute(idCliente, clienteInput);

        // Assert
        assertEquals(idCliente, clienteInput.getId());
        verify(clienteGateway).atualizar(argThat(cliente ->
                cliente.getId().equals(idCliente) &&
                        cliente.getNome().equals("João da Silva") &&
                        cliente.getCpf().equals("12345678901") &&
                        cliente.getDataNascimento().equals(LocalDate.of(1990, 5, 15)) &&
                        cliente.getEndereco().getRua().equals("Rua das Flores")
        ));
    }

    @Test
    void devePropagarsExcecaoQuandoGatewayFalha() {
        // Arrange
        RuntimeException excecaoEsperada = new RuntimeException("Erro ao atualizar cliente no banco de dados");
        when(clienteGateway.atualizar(any(Cliente.class))).thenThrow(excecaoEsperada);

        // Act & Assert
        RuntimeException excecaoLancada = assertThrows(RuntimeException.class, () ->
                atualizarClienteServiceUseCase.execute(idCliente, clienteInput)
        );

        assertEquals("Erro ao atualizar cliente no banco de dados", excecaoLancada.getMessage());
        assertEquals(idCliente, clienteInput.getId());
        verify(clienteGateway, times(1)).atualizar(clienteInput);
    }

    @Test
    void deveFuncionarComClienteQueJaPossuiId() {
        // Arrange
        Endereco outroEndereco = Endereco.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cep("98765-432")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        Cliente clienteComId = Cliente.builder()
                .id(999L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(outroEndereco)
                .build();

        Cliente clienteRetornadoEsperado = Cliente.builder()
                .id(idCliente)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(outroEndereco)
                .build();

        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornadoEsperado);

        // Act
        Cliente resultado = atualizarClienteServiceUseCase.execute(idCliente, clienteComId);

        // Assert
        assertNotNull(resultado);
        assertEquals(idCliente, resultado.getId());
        assertEquals(idCliente, clienteComId.getId());
        verify(clienteGateway, times(1)).atualizar(clienteComId);
    }

    @Test
    void deveChamarGatewayComArgumentosCorretos() {
        // Arrange
        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornado);

        // Act
        atualizarClienteServiceUseCase.execute(idCliente, clienteInput);

        // Assert
        verify(clienteGateway).atualizar(eq(clienteInput));
        verifyNoMoreInteractions(clienteGateway);
    }

    @Test
    void deveFuncionarComClienteSemEndereco() {
        // Arrange
        Cliente clienteSemEndereco = Cliente.builder()
                .nome("Pedro Santos")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1995, 3, 10))
                .endereco(null)
                .build();

        Cliente clienteRetornadoSemEndereco = Cliente.builder()
                .id(idCliente)
                .nome("Pedro Santos")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1995, 3, 10))
                .endereco(null)
                .build();

        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornadoSemEndereco);

        // Act
        Cliente resultado = atualizarClienteServiceUseCase.execute(idCliente, clienteSemEndereco);

        // Assert
        assertNotNull(resultado);
        assertEquals(idCliente, resultado.getId());
        assertEquals("Pedro Santos", resultado.getNome());
        assertEquals("11122233344", resultado.getCpf());
        assertNull(resultado.getEndereco());
        verify(clienteGateway, times(1)).atualizar(clienteSemEndereco);
    }

    @Test
    void deveAtualizarClienteComEnderecoCompleto() {
        // Arrange
        Endereco enderecoCompleto = Endereco.builder()
                .rua("Rua Augusta")
                .numero("1000")
                .cep("01305-100")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        Cliente clienteComEnderecoCompleto = Cliente.builder()
                .nome("Ana Silva")
                .cpf("55566677788")
                .dataNascimento(LocalDate.of(1988, 12, 5))
                .endereco(enderecoCompleto)
                .build();

        Cliente clienteRetornadoCompleto = Cliente.builder()
                .id(idCliente)
                .nome("Ana Silva")
                .cpf("55566677788")
                .dataNascimento(LocalDate.of(1988, 12, 5))
                .endereco(enderecoCompleto)
                .build();

        when(clienteGateway.atualizar(any(Cliente.class))).thenReturn(clienteRetornadoCompleto);

        // Act
        Cliente resultado = atualizarClienteServiceUseCase.execute(idCliente, clienteComEnderecoCompleto);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getEndereco());
        assertEquals("Rua Augusta", resultado.getEndereco().getRua());
        assertEquals("1000", resultado.getEndereco().getNumero());
        assertEquals("01305-100", resultado.getEndereco().getCep());
        assertEquals("São Paulo", resultado.getEndereco().getCidade());
        assertEquals("SP", resultado.getEndereco().getEstado());
        verify(clienteGateway, times(1)).atualizar(clienteComEnderecoCompleto);
    }
}