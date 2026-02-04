CREATE TABLE ordem_servico (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,
    status VARCHAR(50),
    data_criacao TIMESTAMP WITHOUT TIME ZONE,
    data_inicio_da_execucao TIMESTAMP WITHOUT TIME ZONE,
    data_inicio_diagnostico TIMESTAMP WITHOUT TIME ZONE,
    data_fim_diagnostico TIMESTAMP WITHOUT TIME ZONE,
    data_finalizacao TIMESTAMP WITHOUT TIME ZONE,
    data_entrega TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE item_peca_ordem_servico (
    id UUID PRIMARY KEY,
    ordem_servico_id UUID NOT NULL,
    peca_id UUID NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(12, 2) NOT NULL,
    nome VARCHAR (50) NOT NULL,
    descricao VARCHAR (100) NOT NULL,
    CONSTRAINT fk_ipos_ordem_servico FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
);

CREATE TABLE item_servico_ordem_servico (
    id UUID PRIMARY KEY,
    nome varchar(50) NOT NULL,
    descricao varchar(200) NOT NULL,
    ordem_servico_id UUID NOT NULL,
    servico_id UUID NOT NULL,
    preco_unitario NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_isos_ordem_servico FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
);
