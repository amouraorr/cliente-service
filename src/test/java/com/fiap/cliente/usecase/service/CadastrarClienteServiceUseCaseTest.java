package com.fiap.cliente.usecase.service;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.gateway.ClienteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarClienteServiceUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private CadastrarClienteServiceUseCase useCase;

    private Cliente clienteParaCadastro;
    private Cliente clienteExistente;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        clienteParaCadastro = Cliente.builder()
                .cpf("12345678901")
                .nome("João Silva")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        clienteExistente = Cliente.builder()
                .id(1L)
                .cpf("12345678901")
                .nome("João Existente")
                .dataNascimento(LocalDate.of(1985, 3, 20))
                .build();

        clienteSalvo = Cliente.builder()
                .id(2L)
                .cpf("12345678901")
                .nome("João Silva")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();
    }

    @Test
    void deveExecutarComSucessoQuandoCpfNaoExiste() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteParaCadastro))
                .thenReturn(clienteSalvo);

        // When
        Cliente resultado = useCase.execute(clienteParaCadastro);

        // Then
        assertNotNull(resultado);
        assertEquals(clienteSalvo.getId(), resultado.getId());
        assertEquals(clienteSalvo.getCpf(), resultado.getCpf());
        assertEquals(clienteSalvo.getNome(), resultado.getNome());
        assertEquals(clienteSalvo.getDataNascimento(), resultado.getDataNascimento());

        verify(gateway, times(1)).buscarPorCpf(eq(clienteParaCadastro.getCpf()));
        verify(gateway, times(1)).salvar(eq(clienteParaCadastro));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExiste() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(clienteParaCadastro)
        );

        assertEquals("CPF já cadastrado!", exception.getMessage());

        verify(gateway, times(1)).buscarPorCpf(eq(clienteParaCadastro.getCpf()));
        verify(gateway, never()).salvar(any(Cliente.class));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveVerificarSeLogInfoEChamadoParaVerificacaoCpf() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteParaCadastro))
                .thenReturn(clienteSalvo);

        // When
        useCase.execute(clienteParaCadastro);

        // Then
        verify(gateway).buscarPorCpf(clienteParaCadastro.getCpf());
        verify(gateway).salvar(clienteParaCadastro);
    }

    @Test
    void deveVerificarSeLogInfoEChamadoParaClienteSalvo() {
        // Given
        when(gateway.buscarPorCpf(clienteParaCadastro.getCpf()))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteParaCadastro))
                .thenReturn(clienteSalvo);

        // When
        Cliente resultado = useCase.execute(clienteParaCadastro);

        // Then
        assertNotNull(resultado);
        assertEquals(clienteSalvo.getId(), resultado.getId());
        verify(gateway).salvar(clienteParaCadastro);
    }

    @Test
    void deveVerificarComportamentoComCpfNulo() {
        // Given
        Cliente clienteComCpfNulo = Cliente.builder()
                .nome("Cliente Teste")
                .dataNascimento(LocalDate.of(1995, 12, 25))
                .build();

        when(gateway.buscarPorCpf(null))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteComCpfNulo))
                .thenReturn(clienteComCpfNulo);

        // When
        Cliente resultado = useCase.execute(clienteComCpfNulo);

        // Then
        assertNotNull(resultado);
        verify(gateway).buscarPorCpf(null);
        verify(gateway).salvar(clienteComCpfNulo);
    }

    @Test
    void deveVerificarComportamentoComClienteNulo() {
        // When
        assertThrows(NullPointerException.class, () -> {
            useCase.execute(null);
        });

        verifyNoInteractions(gateway);
    }

    @Test
    void deveVerificarComportamentoComDataNascimentoNula() {
        // Given
        Cliente clienteComDataNula = Cliente.builder()
                .cpf("98765432100")
                .nome("Maria Silva")
                .build();

        Cliente clienteSalvoComDataNula = Cliente.builder()
                .id(3L)
                .cpf("98765432100")
                .nome("Maria Silva")
                .build();

        when(gateway.buscarPorCpf(clienteComDataNula.getCpf()))
                .thenReturn(Optional.empty());
        when(gateway.salvar(clienteComDataNula))
                .thenReturn(clienteSalvoComDataNula);

        // When
        Cliente resultado = useCase.execute(clienteComDataNula);

        // Then
        assertNotNull(resultado);
        assertEquals(clienteSalvoComDataNula.getId(), resultado.getId());
        assertEquals(clienteSalvoComDataNula.getCpf(), resultado.getCpf());
        assertEquals(clienteSalvoComDataNula.getNome(), resultado.getNome());
        assertNull(resultado.getDataNascimento());

        verify(gateway).buscarPorCpf(clienteComDataNula.getCpf());
        verify(gateway).salvar(clienteComDataNula);
    }
}