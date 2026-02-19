package br.com.oficina.ordem_servico.core.usecase.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListarOrdensServicoConcluidasUseCaseOutput(
        UUID osId,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataFinalizacao
) {
}
