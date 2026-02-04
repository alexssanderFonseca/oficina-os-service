package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoPeloIdClienteUseCaseOutput;

import java.util.List;
import java.util.UUID;

public interface ListarOrdensServicoPeloIdClienteUseCase {
    List<ListarOrdensServicoPeloIdClienteUseCaseOutput> executar(UUID idCliente);
}