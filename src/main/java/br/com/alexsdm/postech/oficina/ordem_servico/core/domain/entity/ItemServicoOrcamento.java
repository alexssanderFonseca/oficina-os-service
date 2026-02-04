package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ItemServicoOrcamento {
    private UUID id;
    private UUID servicoId;
    private String nome;
    private String descricao;
    private BigDecimal preco;

    public ItemServicoOrcamento(UUID id,
                                UUID servicoId,
                                String nome,
                                String descricao,
                                BigDecimal preco) {
        this.id = id;
        this.servicoId = servicoId;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }
}
