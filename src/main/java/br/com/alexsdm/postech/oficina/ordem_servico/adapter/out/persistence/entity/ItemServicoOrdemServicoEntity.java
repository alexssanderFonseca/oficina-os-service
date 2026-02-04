package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "item_servico_ordem_servico")
@Getter
@Setter
@NoArgsConstructor
public class ItemServicoOrdemServicoEntity {

    @Id
    private UUID id;
    private UUID servicoId;
    private String nome;
    private String descricao;
    private BigDecimal precoUnitario;
    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServicoEntity ordemServico;
}
