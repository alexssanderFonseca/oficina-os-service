package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.pagination.Page;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ListarOrdemServicoUsecase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdemServicoUsecaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
public class ListarOrdemServicoUsecaseImpl implements ListarOrdemServicoUsecase {

    private final OrdemServicoRepository repository;

    @Override
    public Page<ListarOrdemServicoUsecaseOutput> executar(Long pagina,
                                                          Long quantidade) {
        var page = repository.listarTodasOrdenadas(pagina, quantidade);
        var conteudo = page.conteudo()
                .stream()
                .map(ordemServico -> new ListarOrdemServicoUsecaseOutput(
                        ordemServico.getId(),
                        ordemServico.getCliente().getId(),
                        ordemServico.getVeiculo().getId(),
                        ordemServico.getStatus().name(),
                        ordemServico.getDataCriacao()
                ))
                .toList();

        return new Page<>(conteudo,
                page.totalPaginas(),
                page.totalElementos(),
                page.pagina()
        );
    }
}
