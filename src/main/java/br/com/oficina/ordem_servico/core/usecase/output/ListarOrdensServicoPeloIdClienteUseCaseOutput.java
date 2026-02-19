package br.com.oficina.ordem_servico.core.usecase.output;

import java.util.UUID;

public record ListarOrdensServicoPeloIdClienteUseCaseOutput(
        UUID numeroOs,
        String status
) {
}
