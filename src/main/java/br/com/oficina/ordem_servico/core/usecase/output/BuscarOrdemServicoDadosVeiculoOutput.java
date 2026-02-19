package br.com.oficina.ordem_servico.core.usecase.output;

public record BuscarOrdemServicoDadosVeiculoOutput(
        String placa,
        String marca,
        String ano,
        String cor
) {
}