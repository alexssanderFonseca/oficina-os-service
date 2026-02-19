package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.pagination.Page;
import br.com.oficina.ordem_servico.core.port.in.ListarOrdemServicoUsecase;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.oficina.ordem_servico.core.usecase.output.ListarOrdemServicoUsecaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequiredArgsConstructor
public class ListarOrdemServicoUsecaseImpl implements ListarOrdemServicoUsecase {

    private static final Logger logger = LoggerFactory.getLogger(ListarOrdemServicoUsecaseImpl.class);

    private final OrdemServicoRepository repository;

    @Override
    public Page<ListarOrdemServicoUsecaseOutput> executar(Long pagina,
                                                          Long quantidade) {
        logger.info("Listando ordens de serviço paginadas. Página: {}, Quantidade: {}.", pagina, quantidade);
        try {
            var page = repository.listarTodasOrdenadas(pagina, quantidade);
            var conteudo = page.conteudo()
                    .stream()
                    .map(ordemServico -> new ListarOrdemServicoUsecaseOutput(
                            ordemServico.getId(),
                            ordemServico.getCliente().getId(),
                            ordemServico.getVeiculo().getId(),
                            ordemServico.getStatus().name(),
                            ordemServico.getStatusPagamento() != null ? ordemServico.getStatusPagamento().name() : null,
                            ordemServico.getDataCriacao()
                    ))
                    .toList();

            logger.info("Listagem de ordens de serviço concluída. Total de elementos: {}.", page.totalElementos());
            return new Page<>(conteudo,
                    page.totalPaginas(),
                    page.totalElementos(),
                    page.pagina()
            );
        } catch (Exception e) {
            logger.error("Erro ao listar ordens de serviço paginadas (Página: {}, Quantidade: {}): {}", pagina, quantidade, e.getMessage(), e);
            throw e;
        }
    }
}
