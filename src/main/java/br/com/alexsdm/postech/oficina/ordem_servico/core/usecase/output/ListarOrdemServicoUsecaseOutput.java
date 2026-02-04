package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListarOrdemServicoUsecaseOutput(UUID osId,
                                              UUID clienteId,
                                              UUID veiculoId,
                                              String status,
                                              LocalDateTime dataAbertura) {
}
