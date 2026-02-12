-- Drop old tables for parts and services
DROP TABLE IF EXISTS item_peca_ordem_servico;
DROP TABLE IF EXISTS item_servico_ordem_servico;

-- Create new unified item table
CREATE TABLE item_ordem_servico (
    id UUID PRIMARY KEY,
    ordem_servico_id UUID NOT NULL,
    item_id UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    preco NUMERIC(12, 2) NOT NULL,
    quantidade INTEGER,
    tipo VARCHAR(50) NOT NULL,
    CONSTRAINT fk_item_os_ordem_servico FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico(id) ON DELETE CASCADE
);
