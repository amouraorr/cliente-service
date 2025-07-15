package com.fiap.cliente.mapper;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.domain.Endereco;
import com.fiap.cliente.dto.request.ClienteRequestDTO;
import com.fiap.cliente.dto.request.EnderecoRequestDTO;
import com.fiap.cliente.dto.response.ClienteResponseDTO;
import com.fiap.cliente.dto.response.EnderecoResponseDTO;
import com.fiap.cliente.gateway.entity.ClienteEntity;
import com.fiap.cliente.gateway.entity.EnderecoEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteEntity toEntity(Cliente cliente);
    Cliente toDomain(ClienteEntity entity);

    Cliente toDomain(ClienteRequestDTO dto);
    Endereco toDomain(EnderecoRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente cliente);
    EnderecoResponseDTO toResponseDTO(Endereco endereco);

    EnderecoEmbeddable toEmbeddable(Endereco endereco);
    Endereco toDomain(EnderecoEmbeddable embeddable);
}
