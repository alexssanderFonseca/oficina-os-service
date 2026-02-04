package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ItemPecaOrcamento {
    private UUID pecaId;
    private Integer quantidade;
    private String nome;
    private String descricao;
    private BigDecimal preco;

    public ItemPecaOrcamento(UUID pecaId,
                             Integer quantidade,
                             String nome,
                             String descricao,
                             BigDecimal preco) {
        this.pecaId = pecaId;
        this.quantidade = quantidade;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

}
