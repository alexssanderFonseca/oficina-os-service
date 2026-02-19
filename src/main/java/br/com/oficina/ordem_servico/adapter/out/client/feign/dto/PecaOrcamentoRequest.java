package br.com.oficina.ordem_servico.adapter.out.client.feign.dto;

import java.util.UUID;

// Based on Swagger schema
public record PecaOrcamentoRequest(
    UUID pecaId,
    Integer quantidade
) {
}
