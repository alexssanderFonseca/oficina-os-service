package br.com.alexsdm.postech.oficina.ordem_servico.core.port.in;

import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoConcluidasUseCaseOutput;

import java.util.List;

public interface ListarOrdensServicoConcluidasUseCase {

    List<ListarOrdensServicoConcluidasUseCaseOutput> executar();
}
