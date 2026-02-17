package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input;

import java.util.List;
import java.util.UUID;

public record FinalizarDiagnosticoInput(UUID osId,
                                        List<ItemOrdemServicoInput> itens) {


    public record ItemOrdemServicoInput(
            UUID id,
            Integer quantidade,
            String tipo
    ) {
    }

}

