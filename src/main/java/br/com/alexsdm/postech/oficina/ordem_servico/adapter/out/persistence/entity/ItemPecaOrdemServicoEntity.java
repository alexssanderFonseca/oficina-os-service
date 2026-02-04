package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "item_peca_ordem_servico")
@Getter
@Setter
@NoArgsConstructor
public class ItemPecaOrdemServicoEntity {

    @Id
    private UUID id;
    private String nome;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServicoEntity ordemServico;

    private UUID pecaId;
    private BigDecimal precoUnitario;
    private Integer quantidade;
    private String descricao;
}
