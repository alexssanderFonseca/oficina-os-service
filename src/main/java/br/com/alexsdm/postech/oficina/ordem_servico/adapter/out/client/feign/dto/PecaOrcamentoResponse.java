package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PecaOrcamentoResponse(
    UUID id,
    int qtd,
    String nome,
    String descricao,
    BigDecimal valorUnitario
) {
}
