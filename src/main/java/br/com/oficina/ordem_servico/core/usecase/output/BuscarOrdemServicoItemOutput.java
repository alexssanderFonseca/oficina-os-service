package br.com.oficina.ordem_servico.core.usecase.output;

import java.math.BigDecimal;

public record BuscarOrdemServicoItemOutput(
    String nome,
    String descricao,
    BigDecimal preco,
    Integer quantidade,
    String tipo
) {
}
