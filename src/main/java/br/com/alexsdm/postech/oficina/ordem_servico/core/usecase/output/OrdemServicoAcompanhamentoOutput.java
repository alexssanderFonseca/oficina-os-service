package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output;

import java.time.LocalDateTime;

public record OrdemServicoAcompanhamentoOutput(
        Long id,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataInicioDaExecucao,
        LocalDateTime dataFinalizacao,
        LocalDateTime dataEntrega,
        String status
) {
}