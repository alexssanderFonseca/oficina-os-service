package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input;

import java.util.List;
import java.util.UUID;

public record FinalizarDiagnosticoInput(UUID osId,
                                        List<FinalizarDiagnosticoItemPecaInput> pecas,
                                        List<UUID> servicosIds) {

    public record FinalizarDiagnosticoItemPecaInput(UUID pecaId,
                                                    Integer quantidade) {
    }
}


