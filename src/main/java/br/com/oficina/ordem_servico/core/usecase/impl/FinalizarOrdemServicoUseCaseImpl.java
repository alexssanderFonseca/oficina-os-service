package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.oficina.ordem_servico.core.domain.entity.Status;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.oficina.ordem_servico.core.port.in.FinalizarOrdemServicoUseCase;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinalizarOrdemServicoUseCaseImpl implements FinalizarOrdemServicoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(FinalizarOrdemServicoUseCaseImpl.class);

    private final OrdemServicoRepository gateway;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public void executar(UUID id) {
        logger.info("Iniciando finalização da Ordem de Serviço ID: {}.", id);
        try {
            var os = gateway.buscarPorId(id)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada para finalização.", id);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço ID {} encontrada para finalização.", id);

            os.finalizar();
            atualizarMetricaTempoGasto(os);
            gateway.salvar(os);
            logger.info("Ordem de Serviço ID {} finalizada e salva com sucesso.", id);
        } catch (Exception e) {
            logger.error("Erro na finalização da Ordem de Serviço ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private void atualizarMetricaTempoGasto(OrdemServico ordemServico) {
        try {
            var tempoGasto = Duration.between(ordemServico.getDataInicioDaExecucao(), ordemServico.getDataFinalizacao())
                    .toMillis();
            logger.info("Atualizando métrica de tempo gasto para Ordem de Serviço ID {}. Tempo gasto: {}ms na fase '{}'.", ordemServico.getId(), tempoGasto, Status.EM_EXECUCAO.toString());
            ordemServicoMetricPort.registrarTempoGastoFaseOrdemServico(tempoGasto, Status.EM_EXECUCAO.toString());
        } catch (Exception e) {
            logger.error("Erro ao atualizar métrica de tempo gasto para Ordem de Serviço ID {}: {}", ordemServico.getId(), e.getMessage(), e);
        }
    }
}