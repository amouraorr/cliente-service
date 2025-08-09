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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarClientesServiceUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private ListarClientesServiceUseCase useCase;

    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente cliente3;
    private List<Cliente> listaClientes;

    @BeforeEach
    void setUp() {
        Endereco endereco1 = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cep("01234-567")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        Endereco endereco2 = Endereco.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cep("01310-100")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        cliente1 = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco1)
                .build();

        cliente2 = Cliente.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(endereco2)
                .build();

        cliente3 = Cliente.builder()
                .id(3L)
                .nome("Pedro Oliveira")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1992, 3, 8))
                .build();

        listaClientes = Arrays.asList(cliente1, cliente2, cliente3);
    }

    @Test
    void deveRetornarListaDeClientesQuandoExistiremClientes() {
        // Given
        when(gateway.listarTodos()).thenReturn(listaClientes);

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        assertEquals(cliente1.getId(), resultado.get(0).getId());
        assertEquals(cliente1.getNome(), resultado.get(0).getNome());
        assertEquals(cliente1.getCpf(), resultado.get(0).getCpf());
        assertEquals(cliente1.getDataNascimento(), resultado.get(0).getDataNascimento());

        assertEquals(cliente2.getId(), resultado.get(1).getId());
        assertEquals(cliente2.getNome(), resultado.get(1).getNome());
        assertEquals(cliente2.getCpf(), resultado.get(1).getCpf());

        assertEquals(cliente3.getId(), resultado.get(2).getId());
        assertEquals(cliente3.getNome(), resultado.get(2).getNome());
        assertEquals(cliente3.getCpf(), resultado.get(2).getCpf());

        verify(gateway, times(1)).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremClientes() {
        // Given
        when(gateway.listarTodos()).thenReturn(Collections.emptyList());

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.size());

        verify(gateway, times(1)).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarListaComUmClienteQuandoExistirApenasUm() {
        // Given
        List<Cliente> listaComUmCliente = Collections.singletonList(cliente1);
        when(gateway.listarTodos()).thenReturn(listaComUmCliente);

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(cliente1.getId(), resultado.get(0).getId());
        assertEquals(cliente1.getNome(), resultado.get(0).getNome());
        assertEquals(cliente1.getCpf(), resultado.get(0).getCpf());

        verify(gateway, times(1)).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveRetornarListaComClientesSemEndereco() {
        // Given
        Cliente clienteSemEndereco = Cliente.builder()
                .id(4L)
                .nome("Ana Costa")
                .cpf("55566677788")
                .dataNascimento(LocalDate.of(1988, 7, 12))
                .endereco(null)
                .build();

        List<Cliente> listaComClienteSemEndereco = Arrays.asList(cliente1, clienteSemEndereco);
        when(gateway.listarTodos()).thenReturn(listaComClienteSemEndereco);

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(cliente1.getId(), resultado.get(0).getId());
        assertNotNull(resultado.get(0).getEndereco());

        assertEquals(clienteSemEndereco.getId(), resultado.get(1).getId());
        assertNull(resultado.get(1).getEndereco());

        verify(gateway, times(1)).listarTodos();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void deveVerificarQueGatewayEChamadoExatamenteUmaVez() {
        // Given
        when(gateway.listarTodos()).thenReturn(listaClientes);

        // When
        useCase.execute();

        // Then
        verify(gateway, times(1)).listarTodos();
        verify(gateway, only()).listarTodos();
    }

    @Test
    void deveRetornarMesmaListaRetornadaPeloGateway() {
        // Given
        when(gateway.listarTodos()).thenReturn(listaClientes);

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertSame(listaClientes, resultado);
        assertEquals(listaClientes.size(), resultado.size());

        for (int i = 0; i < listaClientes.size(); i++) {
            assertSame(listaClientes.get(i), resultado.get(i));
        }

        verify(gateway).listarTodos();
    }

    @Test
    void deveRetornarListaComClientesComDataNascimentoNula() {
        // Given
        Cliente clienteComDataNula = Cliente.builder()
                .id(5L)
                .nome("Carlos Silva")
                .cpf("99988877766")
                .dataNascimento(null)
                .build();

        List<Cliente> listaComDataNula = Arrays.asList(cliente1, clienteComDataNula);
        when(gateway.listarTodos()).thenReturn(listaComDataNula);

        // When
        List<Cliente> resultado = useCase.execute();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(cliente1.getDataNascimento(), resultado.get(0).getDataNascimento());
        assertNull(resultado.get(1).getDataNascimento());

        verify(gateway).listarTodos();
    }
}