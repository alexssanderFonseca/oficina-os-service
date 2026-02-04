package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ItemServicoOrdemServico {
    private UUID id;
    private UUID servicoId;
    private String nome;
    private String descricao;
    private BigDecimal preco;

    public ItemServicoOrdemServico(UUID servicoId) {
        this.servicoId = servicoId;
    }
}
