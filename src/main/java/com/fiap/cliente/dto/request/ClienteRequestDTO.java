package com.fiap.cliente.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private EnderecoRequestDTO endereco;
}
