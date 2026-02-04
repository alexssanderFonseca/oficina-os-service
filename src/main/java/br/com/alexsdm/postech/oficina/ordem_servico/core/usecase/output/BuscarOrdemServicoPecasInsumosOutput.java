package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output;

public record BuscarOrdemServicoPecasInsumosOutput(
        String nome,
        String descricao,
        Integer quantidade
) {
}