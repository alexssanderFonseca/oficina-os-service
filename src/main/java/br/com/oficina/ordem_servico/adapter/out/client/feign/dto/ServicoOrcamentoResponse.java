package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoOrcamentoResponse(
    UUID id,
    String nome,
    String descricao,
    BigDecimal valor
) {
}
