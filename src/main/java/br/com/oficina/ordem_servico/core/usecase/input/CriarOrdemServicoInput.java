package br.com.oficina.ordem_servico.core.usecase.input;

import java.util.List;
import java.util.UUID;

public record CriarOrdemServicoInput(
        UUID clienteId,
        UUID veiculoId,
        List<ItemInput> itens
) {

    public CriarOrdemServicoInput(UUID clienteId, UUID veiculoId, List<ItemInput> itens) {
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.itens = itens != null ? itens : List.of();
    }


    public record ItemInput(
            UUID id,
            Integer quantidade,
            String tipo
    ) {
    }


}
