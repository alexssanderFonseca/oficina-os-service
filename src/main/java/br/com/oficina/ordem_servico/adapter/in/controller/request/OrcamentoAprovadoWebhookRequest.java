package br.com.oficina.ordem_servico.adapter.in.controller.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrcamentoAprovadoWebhookRequest(
        @NotNull UUID orcamentoId,
        @NotNull UUID ordemServicoId
) { }