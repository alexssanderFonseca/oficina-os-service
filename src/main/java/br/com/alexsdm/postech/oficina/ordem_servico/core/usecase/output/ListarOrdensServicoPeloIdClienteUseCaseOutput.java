package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output;

import java.util.UUID;

public record ListarOrdensServicoPeloIdClienteUseCaseOutput(
        UUID numeroOs,
        String status
) {
}
