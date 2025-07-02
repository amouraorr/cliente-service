package com.fiap.cliente.domain;

import jakarta.persistence.*;

@Entity
public class Endereco {
    private String rua;
    private String numero;
    private String cep;
    private String cidade;
    private String estado;
    // getters e setters
}