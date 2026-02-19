package br.com.oficina.ordem_servico.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "item_ordem_servico")
public class ItemOrdemServicoEntity {

    @Id
    private UUID id;

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    private UUID itemId;

    @Enumerated(EnumType.STRING)
    private TipoItemEntity tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServicoEntity ordemServico;
}
