package br.com.oficina.ordem_servico.core.usecase.output;

import java.util.UUID;

public record FinalizarDiagnosticoOutput(
        UUID orcamentoId
) {
}