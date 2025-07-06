package com.fiap.pagamento.mapper;

import com.fiap.pagamento.domain.Cliente;
import com.fiap.pagamento.domain.Endereco;
import com.fiap.pagamento.dto.request.ClienteRequestDTO;
import com.fiap.pagamento.dto.request.EnderecoRequestDTO;
import com.fiap.pagamento.dto.response.ClienteResponseDTO;
import com.fiap.pagamento.dto.response.EnderecoResponseDTO;
import com.fiap.pagamento.gateway.entity.ClienteEntity;
import com.fiap.pagamento.gateway.entity.EnderecoEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    // Domínio <-> Entity
    ClienteEntity toEntity(Cliente cliente);
    Cliente toDomain(ClienteEntity entity);

    // RequestDTO <-> Domínio
    Cliente toDomain(ClienteRequestDTO dto);
    Endereco toDomain(EnderecoRequestDTO dto);

    // Domínio <-> ResponseDTO
    ClienteResponseDTO toResponseDTO(Cliente cliente);
    EnderecoResponseDTO toResponseDTO(Endereco endereco);

    // Endereco mapping
    EnderecoEmbeddable toEmbeddable(Endereco endereco);
    Endereco toDomain(EnderecoEmbeddable embeddable);
}
