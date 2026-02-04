package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record Servico(
        UUID id,
        String nome,
        String descricao,
        BigDecimal preco
) {
}
