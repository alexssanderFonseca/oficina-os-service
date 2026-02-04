package br.com.alexsdm.postech.oficina.ordem_servico.adapter.in.controller.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CriarOrdemDeServicoRequest(@NotNull UUID veiculoId,
                                         @NotNull UUID clienteId,
                                         List<ItemPecaInsumoRequest> pecasInsumos,
                                         List<UUID> servicos) {

    public record ItemPecaInsumoRequest(UUID idPecaInsumo, int qtd) {
    }

}

