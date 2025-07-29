package com.fiap.cliente.controller;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.dto.request.ClienteRequestDTO;
import com.fiap.cliente.dto.request.EnderecoRequestDTO;
import com.fiap.cliente.dto.response.ClienteResponseDTO;
import com.fiap.cliente.dto.response.EnderecoResponseDTO;
import com.fiap.cliente.mapper.ClienteMapper;
import com.fiap.cliente.usecase.service.AtualizarClienteServiceUseCase;
import com.fiap.cliente.usecase.service.BuscarClientePorCpfServiceUseCase;
import com.fiap.cliente.usecase.service.CadastrarClienteServiceUseCase;
import com.fiap.cliente.usecase.service.ListarClientesServiceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private CadastrarClienteServiceUseCase cadastrarUseCase;

    @Mock
    private AtualizarClienteServiceUseCase atualizarUseCase;

    @Mock
    private BuscarClientePorCpfServiceUseCase buscarPorCpfUseCase;

    @Mock
    private ListarClientesServiceUseCase listarUseCase;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteController clienteController;

    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;
    private Cliente cliente;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = createClienteRequestDTO();
        clienteResponseDTO = createClienteResponseDTO();
        cliente = createCliente();
        clienteSalvo = createClienteSalvo();
    }

    @Test
    void deveCadastrarClienteComSucesso() {
        // Arrange
        when(mapper.toDomain(clienteRequestDTO)).thenReturn(cliente);
        when(cadastrarUseCase.execute(cliente)).thenReturn(clienteSalvo);
        when(mapper.toResponseDTO(clienteSalvo)).thenReturn(clienteResponseDTO);

        // Act
        ResponseEntity<ClienteResponseDTO> response = clienteController.cadastrar(clienteRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteResponseDTO, response.getBody());

        verify(mapper).toDomain(clienteRequestDTO);
        verify(cadastrarUseCase).execute(cliente);
        verify(mapper).toResponseDTO(clienteSalvo);
    }

    @Test
    void deveBuscarClientePorCpfComSucesso() {
        // Arrange
        String cpf = "12345678901";
        when(buscarPorCpfUseCase.execute(cpf)).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDTO(cliente)).thenReturn(clienteResponseDTO);

        // Act
        ResponseEntity<ClienteResponseDTO> response = clienteController.buscarPorCpf(cpf);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteResponseDTO, response.getBody());

        verify(buscarPorCpfUseCase).execute(cpf);
        verify(mapper).toResponseDTO(cliente);
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExistirPorCpf() {
        // Arrange
        String cpf = "12345678901";
        when(buscarPorCpfUseCase.execute(cpf)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ClienteResponseDTO> response = clienteController.buscarPorCpf(cpf);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(buscarPorCpfUseCase).execute(cpf);
        verify(mapper, never()).toResponseDTO((Cliente) any());
    }

    @Test
    void deveListarTodosOsClientesComSucesso() {
        // Arrange
        Cliente outroCliente = createOutroCliente();
        ClienteResponseDTO outroClienteResponseDTO = createOutroClienteResponseDTO();

        List<Cliente> clientes = Arrays.asList(cliente, outroCliente);

        when(listarUseCase.execute()).thenReturn(clientes);
        when(mapper.toResponseDTO(cliente)).thenReturn(clienteResponseDTO);
        when(mapper.toResponseDTO(outroCliente)).thenReturn(outroClienteResponseDTO);

        // Act
        List<ClienteResponseDTO> response = clienteController.listarTodos();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(clienteResponseDTO, response.get(0));
        assertEquals(outroClienteResponseDTO, response.get(1));

        verify(listarUseCase).execute();
        verify(mapper).toResponseDTO(cliente);
        verify(mapper).toResponseDTO(outroCliente);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        // Arrange
        when(listarUseCase.execute()).thenReturn(Arrays.asList());

        // Act
        List<ClienteResponseDTO> response = clienteController.listarTodos();

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(listarUseCase).execute();
        verify(mapper, never()).toResponseDTO(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Long id = 1L;
        Cliente clienteAtualizado = createClienteAtualizado();

        when(mapper.toDomain(clienteRequestDTO)).thenReturn(cliente);
        when(atualizarUseCase.execute(id, cliente)).thenReturn(clienteAtualizado);
        when(mapper.toResponseDTO(clienteAtualizado)).thenReturn(clienteResponseDTO);

        // Act
        ResponseEntity<ClienteResponseDTO> response = clienteController.atualizar(id, clienteRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clienteResponseDTO, response.getBody());

        verify(mapper).toDomain(clienteRequestDTO);
        verify(atualizarUseCase).execute(id, cliente);
        verify(mapper).toResponseDTO(clienteAtualizado);
    }

    @Test
    void deveVerificarSeInteracoesEstaoCorretasNoCadastro() {
        // Arrange
        when(mapper.toDomain(any(ClienteRequestDTO.class))).thenReturn(cliente);
        when(cadastrarUseCase.execute(any(Cliente.class))).thenReturn(clienteSalvo);
        when(mapper.toResponseDTO(any(Cliente.class))).thenReturn(clienteResponseDTO);

        // Act
        clienteController.cadastrar(clienteRequestDTO);

        // Assert
        verify(mapper, times(1)).toDomain(clienteRequestDTO);
        verify(cadastrarUseCase, times(1)).execute(cliente);
        verify(mapper, times(1)).toResponseDTO(clienteSalvo);
        verifyNoMoreInteractions(mapper, cadastrarUseCase);
    }

    @Test
    void deveVerificarSeInteracoesEstaoCorretasNaBuscaPorCpf() {
        // Arrange
        String cpf = "12345678901";
        when(buscarPorCpfUseCase.execute(anyString())).thenReturn(Optional.of(cliente));
        when(mapper.toResponseDTO(any(Cliente.class))).thenReturn(clienteResponseDTO);

        // Act
        clienteController.buscarPorCpf(cpf);

        // Assert
        verify(buscarPorCpfUseCase, times(1)).execute(cpf);
        verify(mapper, times(1)).toResponseDTO(cliente);
        verifyNoMoreInteractions(buscarPorCpfUseCase, mapper);
    }

    @Test
    void deveVerificarSeInteracoesEstaoCorretasNaAtualizacao() {
        // Arrange
        Long id = 1L;
        Cliente clienteAtualizado = createClienteAtualizado();

        when(mapper.toDomain(any(ClienteRequestDTO.class))).thenReturn(cliente);
        when(atualizarUseCase.execute(anyLong(), any(Cliente.class))).thenReturn(clienteAtualizado);
        when(mapper.toResponseDTO(any(Cliente.class))).thenReturn(clienteResponseDTO);

        // Act
        clienteController.atualizar(id, clienteRequestDTO);

        // Assert
        verify(mapper, times(1)).toDomain(clienteRequestDTO);
        verify(atualizarUseCase, times(1)).execute(id, cliente);
        verify(mapper, times(1)).toResponseDTO(clienteAtualizado);
        verifyNoMoreInteractions(mapper, atualizarUseCase);
    }

    private ClienteRequestDTO createClienteRequestDTO() {
        return ClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .endereco(createEnderecoRequestDTO())
                .build();
    }

    private EnderecoRequestDTO createEnderecoRequestDTO() {
        return EnderecoRequestDTO.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cep("01234567")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    private ClienteRequestDTO createOutroClienteRequestDTO() {
        return ClienteRequestDTO.builder()
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 5, 20))
                .endereco(createOutroEnderecoRequestDTO())
                .build();
    }

    private EnderecoRequestDTO createOutroEnderecoRequestDTO() {
        return EnderecoRequestDTO.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cep("01310100")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    private ClienteResponseDTO createClienteResponseDTO() {
        return ClienteResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .endereco(createEnderecoResponseDTO())
                .build();
    }

    private ClienteResponseDTO createOutroClienteResponseDTO() {
        return ClienteResponseDTO.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 5, 20))
                .endereco(createOutroEnderecoResponseDTO())
                .build();
    }

    private EnderecoResponseDTO createEnderecoResponseDTO() {
        return EnderecoResponseDTO.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cep("01234567")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    private EnderecoResponseDTO createOutroEnderecoResponseDTO() {
        return EnderecoResponseDTO.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cep("01310100")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    private Cliente createCliente() {
        return Cliente.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .build();
    }

    private Cliente createClienteSalvo() {
        return Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .build();
    }

    private Cliente createClienteAtualizado() {
        return Cliente.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .build();
    }

    private Cliente createOutroCliente() {
        return Cliente.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 5, 20))
                .build();
    }
}