package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemOrcamentoRequest(
    @NotNull UUID itemId,
    @NotNull Integer quantidade,
    @NotNull String tipo,
    @NotNull String nome,
    String descricao,
    @NotNull BigDecimal precoVenda
) {
}

