package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ListarOrdensServicoConcluidasUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoConcluidasUseCaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Named
@RequiredArgsConstructor
public class ListarOrdensServicoConcluidasUseCaseImpl implements ListarOrdensServicoConcluidasUseCase {

    private final OrdemServicoRepository repository;

    @Override
    public List<ListarOrdensServicoConcluidasUseCaseOutput> executar() {
        return repository.buscarFinalizadas()
                .stream()
                .map(ordemServico -> new ListarOrdensServicoConcluidasUseCaseOutput(
                        ordemServico.getId(),
                        ordemServico.getDataInicioDaExecucao(),
                        ordemServico.getDataFinalizacao()
                )).toList();
    }
}
