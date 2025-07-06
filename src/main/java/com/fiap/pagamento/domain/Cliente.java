package com.fiap.pagamento.domain;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private Endereco endereco;
}
