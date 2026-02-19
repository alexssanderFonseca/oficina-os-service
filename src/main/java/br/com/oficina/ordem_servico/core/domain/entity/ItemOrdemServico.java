package br.com.oficina.ordem_servico.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOrdemServico {
    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    private UUID itemId;
    private TipoItem tipo;


    public BigDecimal getValorTotal() {
        return preco.multiply(BigDecimal.valueOf(quantidade)).setScale(2,
                RoundingMode.HALF_EVEN);
    }
}
