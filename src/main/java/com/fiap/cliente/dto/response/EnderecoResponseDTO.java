package com.fiap.cliente.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponseDTO {

    private String rua;
    private String numero;
    private String cep;
    private String cidade;
    private String estado;
}