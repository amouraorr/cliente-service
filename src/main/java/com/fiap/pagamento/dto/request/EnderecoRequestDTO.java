package com.fiap.pagamento.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoRequestDTO {

    private String rua;
    private String numero;
    private String cep;
    private String cidade;
    private String estado;
}