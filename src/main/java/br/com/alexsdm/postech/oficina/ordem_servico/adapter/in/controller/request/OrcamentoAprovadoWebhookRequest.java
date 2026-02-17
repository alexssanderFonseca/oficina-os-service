package br.com.alexsdm.postech.oficina.ordem_servico.adapter.in.controller.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrcamentoAprovadoWebhookRequest(
        @NotNull UUID orcamentoId,
        @NotNull UUID ordemServicoId
) { }