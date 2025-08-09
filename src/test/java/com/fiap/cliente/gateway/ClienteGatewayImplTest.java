package com.fiap.cliente.gateway;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.domain.Endereco;
import com.fiap.cliente.gateway.entity.ClienteEntity;
import com.fiap.cliente.gateway.entity.EnderecoEmbeddable;
import com.fiap.cliente.gateway.repository.ClienteRepository;
import com.fiap.cliente.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteGatewayImpl clienteGateway;

    private Cliente cliente;
    private ClienteEntity clienteEntity;
    private Endereco endereco;
    private EnderecoEmbeddable enderecoEmbeddable;

    @BeforeEach
    void setUp() {
        endereco = Endereco.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cidade("São Paulo")
                .cep("01234-567")
                .estado("SP")
                .build();

        enderecoEmbeddable = EnderecoEmbeddable.builder()
                .rua("Rua das Flores")
                .numero("123")
                .cidade("São Paulo")
                .cep("01234-567")
                .estado("SP")
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(endereco)
                .build();

        clienteEntity = ClienteEntity.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(enderecoEmbeddable)
                .build();
    }

    @Test
    void deveRetornarClienteSalvoComSucesso() {
        // Given
        when(mapper.toEntity(cliente)).thenReturn(clienteEntity);
        when(repository.save(clienteEntity)).thenReturn(clienteEntity);
        when(mapper.toDomain(clienteEntity)).thenReturn(cliente);

        // When
        Cliente resultado = clienteGateway.salvar(cliente);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.getDataNascimento()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(resultado.getEndereco()).isNotNull();
        assertThat(resultado.getEndereco().getRua()).isEqualTo("Rua das Flores");

        verify(mapper).toEntity(cliente);
        verify(repository).save(clienteEntity);
        verify(mapper).toDomain(clienteEntity);
    }

    @Test
    void deveRetornarClienteQuandoBuscarPorCpfExistente() {
        // Given
        String cpf = "12345678901";
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(clienteEntity));
        when(mapper.toDomain(clienteEntity)).thenReturn(cliente);

        // When
        Optional<Cliente> resultado = clienteGateway.buscarPorCpf(cpf);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCpf()).isEqualTo(cpf);
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
        assertThat(resultado.get().getDataNascimento()).isEqualTo(LocalDate.of(1990, 5, 15));

        verify(repository).findByCpf(cpf);
        verify(mapper).toDomain(clienteEntity);
    }

    @Test
    void deveRetornarOptionalVazioQuandoBuscarPorCpfInexistente() {
        // Given
        String cpf = "99999999999";
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = clienteGateway.buscarPorCpf(cpf);

        // Then
        assertThat(resultado).isEmpty();

        verify(repository).findByCpf(cpf);
    }

    @Test
    void deveRetornarClienteQuandoBuscarPorIdExistente() {
        // Given
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(clienteEntity));
        when(mapper.toDomain(clienteEntity)).thenReturn(cliente);

        // When
        Optional<Cliente> resultado = clienteGateway.buscarPorId(id);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
        assertThat(resultado.get().getCpf()).isEqualTo("12345678901");

        verify(repository).findById(id);
        verify(mapper).toDomain(clienteEntity);
    }

    @Test
    void deveRetornarOptionalVazioQuandoBuscarPorIdInexistente() {
        // Given
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = clienteGateway.buscarPorId(id);

        // Then
        assertThat(resultado).isEmpty();

        verify(repository).findById(id);
    }

    @Test
    void deveRetornarListaDeClientesQuandoListarTodos() {
        // Given
        Endereco segundoEndereco = Endereco.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cidade("São Paulo")
                .cep("01310-100")
                .estado("SP")
                .build();

        EnderecoEmbeddable segundoEnderecoEmbeddable = EnderecoEmbeddable.builder()
                .rua("Avenida Paulista")
                .numero("456")
                .cidade("São Paulo")
                .cep("01310-100")
                .estado("SP")
                .build();

        Cliente segundoCliente = Cliente.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(segundoEndereco)
                .build();

        ClienteEntity segundaEntity = ClienteEntity.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432100")
                .dataNascimento(LocalDate.of(1985, 10, 20))
                .endereco(segundoEnderecoEmbeddable)
                .build();

        List<ClienteEntity> entities = Arrays.asList(clienteEntity, segundaEntity);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDomain(clienteEntity)).thenReturn(cliente);
        when(mapper.toDomain(segundaEntity)).thenReturn(segundoCliente);

        // When
        List<Cliente> resultado = clienteGateway.listarTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        assertThat(resultado.get(0).getCpf()).isEqualTo("12345678901");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria Santos");
        assertThat(resultado.get(1).getCpf()).isEqualTo("98765432100");

        verify(repository).findAll();
        verify(mapper).toDomain(clienteEntity);
        verify(mapper).toDomain(segundaEntity);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverClientes() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Cliente> resultado = clienteGateway.listarTodos();

        // Then
        assertThat(resultado).isEmpty();

        verify(repository).findAll();
    }

    @Test
    void deveRetornarClienteAtualizadoComSucesso() {
        // Given
        Endereco enderecoAtualizado = Endereco.builder()
                .rua("Rua Nova")
                .numero("789")
                .cidade("Rio de Janeiro")
                .cep("20000-000")
                .estado("RJ")
                .build();

        EnderecoEmbeddable enderecoEmbeddableAtualizado = EnderecoEmbeddable.builder()
                .rua("Rua Nova")
                .numero("789")
                .cidade("Rio de Janeiro")
                .cep("20000-000")
                .estado("RJ")
                .build();

        Cliente clienteAtualizado = Cliente.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(enderecoAtualizado)
                .build();

        ClienteEntity entityAtualizada = ClienteEntity.builder()
                .id(1L)
                .nome("João Silva Atualizado")
                .cpf("12345678901")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .endereco(enderecoEmbeddableAtualizado)
                .build();

        when(mapper.toEntity(clienteAtualizado)).thenReturn(entityAtualizada);
        when(repository.save(entityAtualizada)).thenReturn(entityAtualizada);
        when(mapper.toDomain(entityAtualizada)).thenReturn(clienteAtualizado);

        // When
        Cliente resultado = clienteGateway.atualizar(clienteAtualizado);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.getDataNascimento()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(resultado.getEndereco().getRua()).isEqualTo("Rua Nova");
        assertThat(resultado.getEndereco().getCidade()).isEqualTo("Rio de Janeiro");

        verify(mapper).toEntity(clienteAtualizado);
        verify(repository).save(entityAtualizada);
        verify(mapper).toDomain(entityAtualizada);
    }
}