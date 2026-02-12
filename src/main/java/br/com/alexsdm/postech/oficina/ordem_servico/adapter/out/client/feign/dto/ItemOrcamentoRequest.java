package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.client.feign.dto;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.TipoItem;

import java.util.UUID;

public record ItemOrcamentoRequest(
    UUID itemId,
    Integer quantidade,
    TipoItem tipo
) {
}
