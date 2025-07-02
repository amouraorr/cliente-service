package com.fiap.cliente.mapper;

import com.fiap.cliente.domain.Cliente;
import com.fiap.cliente.dto.ClienteDTO;
import com.fiap.cliente.gateway.entity.ClienteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    // Domínio <-> Entity
    ClienteEntity toEntity(Cliente cliente);
    Cliente toDomain(ClienteEntity entity);

    // DTO <-> Domínio
    Cliente toDomain(ClienteDTO dto);
    ClienteDTO toDTO(Cliente cliente);
}