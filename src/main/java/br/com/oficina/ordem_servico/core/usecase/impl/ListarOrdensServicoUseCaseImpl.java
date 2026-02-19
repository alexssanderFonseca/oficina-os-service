package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.port.in.ListarOrdensServicoPeloIdClienteUseCase;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.oficina.ordem_servico.core.usecase.output.ListarOrdensServicoPeloIdClienteUseCaseOutput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class ListarOrdensServicoUseCaseImpl implements ListarOrdensServicoPeloIdClienteUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ListarOrdensServicoUseCaseImpl.class);

    private final OrdemServicoRepository repository;

    @Override
    public List<ListarOrdensServicoPeloIdClienteUseCaseOutput> executar(UUID idCliente) {
        logger.info("Listando ordens de serviço para Cliente ID: {}.", idCliente);
        try {
            List<ListarOrdensServicoPeloIdClienteUseCaseOutput> result = repository.buscarPeloIdCliente(idCliente)
                    .stream()
                    .map(ordemServico -> new ListarOrdensServicoPeloIdClienteUseCaseOutput(
                            ordemServico.getId(),
                            ordemServico.getStatus().name()
                    ))
                    .toList();
            logger.info("{} ordens de serviço encontradas para Cliente ID {}.", result.size(), idCliente);
            return result;
        } catch (Exception e) {
            logger.error("Erro ao listar ordens de serviço para Cliente ID {}: {}", idCliente, e.getMessage(), e);
            throw e;
        }
    }
}