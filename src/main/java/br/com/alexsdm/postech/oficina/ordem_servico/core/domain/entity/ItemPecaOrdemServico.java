package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPecaOrdemServico {
    private UUID id;
    private UUID pecaId;
    private String nome;
    private BigDecimal preco;
    private String descricao;
    private Integer quantidade;

    public ItemPecaOrdemServico(UUID pecaId,
                                Integer quantidade) {
        this.pecaId = pecaId;
        this.quantidade = quantidade;
    }
}
