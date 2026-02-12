package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemCatalogoDto(
        UUID id,
        String nome,
        String descricao,
        Integer quantidadeEstoque,
        BigDecimal precoVenda,
        String tipo) {
}
