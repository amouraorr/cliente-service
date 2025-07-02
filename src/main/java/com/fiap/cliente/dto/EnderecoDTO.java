package com.fiap.cliente.dto;

import lombok.Data;

@Data
public class EnderecoDTO {
    private Long id;
    private String rua;
    private String numero;
    private String cep;
    private String cidade;
    private String estado;
}