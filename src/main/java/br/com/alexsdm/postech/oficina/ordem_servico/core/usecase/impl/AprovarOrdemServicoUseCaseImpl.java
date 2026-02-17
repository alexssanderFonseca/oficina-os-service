package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.AprovarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Named
@RequiredArgsConstructor
public class AprovarOrdemServicoUseCaseImpl implements AprovarOrdemServicoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AprovarOrdemServicoUseCaseImpl.class);

    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public void executar(UUID osId) {
        logger.info("Iniciando aprovação da Ordem de Serviço ID: {}.", osId);
        try {
            var ordemServico = ordemServicoRepository.buscarPorId(osId)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada para aprovação.", osId);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço ID {} encontrada para aprovação.", osId);

            ordemServico.aprovar();
            ordemServicoRepository.salvar(ordemServico);
            logger.info("Ordem de Serviço ID {} aprovada e salva com sucesso.", osId);
        } catch (Exception e) {
            logger.error("Erro na aprovação da Ordem de Serviço ID {}: {}", osId, e.getMessage(), e);
            throw e;
        }
    }
}
