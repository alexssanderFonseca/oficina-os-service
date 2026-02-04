package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output;

import java.util.UUID;

public record FinalizarDiagnosticoOutput(
        UUID orcamentoId
) {
}