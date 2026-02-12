package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Based on the 'PecaInsumo' domain entity and old adapter logic
public record PecaInsumoResponse(
    UUID id,
    String nome,
    String descricao,
    Integer quantidadeEstoque,
    BigDecimal precoVenda
) {
}
