package br.com.oficina.ordem_servico.adapter.in.controller.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CriarOrdemDeServicoRequest(@NotNull UUID veiculoId,
                                         @NotNull UUID clienteId,
                                         List<ItemRequest> itens) {

    public CriarOrdemDeServicoRequest(UUID veiculoId, UUID clienteId, List<ItemRequest> itens) {
        this.veiculoId = veiculoId;
        this.clienteId = clienteId;
        this.itens = itens != null ? itens : List.of();
    }

    public record ItemRequest(UUID id, int quantidade, String tipo) {
    }

}

