package br.com.oficina.ordem_servico.core.port.in;

import br.com.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoOutput;

import java.util.UUID;

public interface BuscarOrdemServicoPorIdUseCase {
    BuscarOrdemServicoOutput executar(UUID id);
}