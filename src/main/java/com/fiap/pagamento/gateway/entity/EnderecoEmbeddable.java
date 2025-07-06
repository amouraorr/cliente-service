package com.fiap.pagamento.gateway.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoEmbeddable {
    private String rua;
    private String numero;
    private String cep;
    private String cidade;
    private String estado;
}