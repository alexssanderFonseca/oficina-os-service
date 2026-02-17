package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ListarOrdensServicoConcluidasUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoConcluidasUseCaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Named
@RequiredArgsConstructor
public class ListarOrdensServicoConcluidasUseCaseImpl implements ListarOrdensServicoConcluidasUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ListarOrdensServicoConcluidasUseCaseImpl.class);

    private final OrdemServicoRepository repository;

    @Override
    public List<ListarOrdensServicoConcluidasUseCaseOutput> executar() {
        logger.info("Listando ordens de serviço concluídas.");
        try {
            List<ListarOrdensServicoConcluidasUseCaseOutput> result = repository.buscarFinalizadas()
                    .stream()
                    .map(ordemServico -> new ListarOrdensServicoConcluidasUseCaseOutput(
                            ordemServico.getId(),
                            ordemServico.getDataInicioDaExecucao(),
                            ordemServico.getDataFinalizacao()
                    )).toList();
            logger.info("{} ordens de serviço concluídas encontradas.", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Erro ao listar ordens de serviço concluídas: {}", e.getMessage(), e);
            throw e;
        }
    }
}
