package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Based on the 'Servico' domain entity and old adapter logic
public record ServicoResponse(
    UUID id,
    String nome,
    String descricao,
    BigDecimal preco
) {
}
