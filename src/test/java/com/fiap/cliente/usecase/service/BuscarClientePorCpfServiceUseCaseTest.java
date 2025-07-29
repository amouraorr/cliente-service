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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarClientePorCpfServiceUseCaseTest {

    @Mock
    private ClienteGateway gateway;

    @InjectMocks
    private BuscarClientePorCpfServiceUseCase service;

    private Cliente clienteMock;
    private String cpfValido;
    private String cpfInexistente;

    @BeforeEach
    void setUp() {
        cpfValido = "12345678901";
        cpfInexistente = "99999999999";

        Endereco endereco = Endereco.builder()
                .rua("Rua das Flores, 123")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .build();

        clienteMock = Cliente.builder()
                .id(1L)
                .nome("José da Silva")
                .cpf(cpfValido)
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();
    }

    @Test
    void deveRetornarClienteQuandoCpfExistir() {
        // Arrange
        when(gateway.buscarPorCpf(cpfValido)).thenReturn(Optional.of(clienteMock));

        // Act
        Optional<Cliente> resultado = service.execute(cpfValido);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(clienteMock);
        assertThat(resultado.get().getCpf()).isEqualTo(cpfValido);
        assertThat(resultado.get().getNome()).isEqualTo("José da Silva");

        verify(gateway, times(1)).buscarPorCpf(cpfValido);
    }

    @Test
    void deveRetornarOptionalVazioQuandoCpfNaoExistir() {
        // Arrange
        when(gateway.buscarPorCpf(cpfInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = service.execute(cpfInexistente);

        // Assert
        assertThat(resultado).isEmpty();

        verify(gateway, times(1)).buscarPorCpf(cpfInexistente);
    }

    @Test
    void deveRetornarOptionalVazioQuandoCpfForNulo() {
        // Arrange
        when(gateway.buscarPorCpf(null)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = service.execute(null);

        // Assert
        assertThat(resultado).isEmpty();

        verify(gateway, times(1)).buscarPorCpf(null);
    }

    @Test
    void deveRetornarOptionalVazioQuandoCpfForVazio() {
        // Arrange
        String cpfVazio = "";
        when(gateway.buscarPorCpf(cpfVazio)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = service.execute(cpfVazio);

        // Assert
        assertThat(resultado).isEmpty();

        verify(gateway, times(1)).buscarPorCpf(cpfVazio);
    }

    @Test
    void devePassarCpfCorretoParaOGateway() {
        // Arrange
        String cpfTeste = "11122233344";
        when(gateway.buscarPorCpf(cpfTeste)).thenReturn(Optional.empty());

        // Act
        service.execute(cpfTeste);

        // Assert
        verify(gateway, times(1)).buscarPorCpf(eq(cpfTeste));
        verify(gateway, never()).buscarPorCpf(argThat(cpf -> !cpfTeste.equals(cpf)));
    }

    @Test
    void deveChamarGatewayApenasUmaVez() {
        // Arrange
        when(gateway.buscarPorCpf(anyString())).thenReturn(Optional.of(clienteMock));

        // Act
        service.execute(cpfValido);

        // Assert
        verify(gateway, times(1)).buscarPorCpf(anyString());
        verifyNoMoreInteractions(gateway);
    }
}