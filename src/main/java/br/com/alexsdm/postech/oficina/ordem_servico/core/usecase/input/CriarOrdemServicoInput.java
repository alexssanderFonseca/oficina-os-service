package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input;

import java.util.List;
import java.util.UUID;

public record CriarOrdemServicoInput(
        UUID clienteId,
        UUID veiculoId,
        List<CriarOrdemServicoItemInsumoInput> pecasInsumos,
        List<UUID> servicos
) {


    public record CriarOrdemServicoItemInsumoInput(UUID idPecaInsumo, int qtd) {

    }

}
