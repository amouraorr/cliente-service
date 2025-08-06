CREATE TABLE clientes (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(255),
                           cpf VARCHAR(20) not null ,
                           dataNascimento DATE,
                           rua VARCHAR(255),
                           numero VARCHAR(50),
                           cep VARCHAR(20),
                           cidade VARCHAR(100),
                           estado VARCHAR(50)
);