package br.com.oficina.ordem_servico.core.usecase.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BuscarOrdemServicoOutput(
        UUID id,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataInicioDaExecucao,
        LocalDateTime dataFinalizacao,
        LocalDateTime dataEntrega,
        String status,
        String statusPagamento,
        BuscarOrdemServicoDadosClientOutput dadosCliente,
        BuscarOrdemServicoDadosVeiculoOutput dadosVeiculo,
        List<BuscarOrdemServicoItemOutput> itens
) {
}