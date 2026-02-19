package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.oficina.ordem_servico.core.domain.entity.Status;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.oficina.ordem_servico.core.port.in.EntregarOrdemServicoUseCase;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregarOrdemServicoUseCaseImpl implements EntregarOrdemServicoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(EntregarOrdemServicoUseCaseImpl.class);

    private final OrdemServicoRepository gateway;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public void executar(UUID id) {
        logger.info("Iniciando entrega da Ordem de Serviço ID: {}.", id);
        try {
            var os = gateway.buscarPorId(id)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada para entrega.", id);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço ID {} encontrada para entrega.", id);

            os.entregar();
            atualizarMetricaTempoGasto(os);
            gateway.salvar(os);
            logger.info("Ordem de Serviço ID {} entregue e salva com sucesso.", id);
        } catch (Exception e) {
            logger.error("Erro na entrega da Ordem de Serviço ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private void atualizarMetricaTempoGasto(OrdemServico ordemServico) {
        try {
            var tempoGasto = Duration.between(ordemServico.getDataFinalizacao(), ordemServico.getDataEntrega())
                    .toMillis();
            logger.info("Atualizando métrica de tempo gasto para Ordem de Serviço ID {}. Tempo gasto: {}ms na fase '{}'.", ordemServico.getId(), tempoGasto, Status.ENTREGUE.toString());
            ordemServicoMetricPort.registrarTempoGastoFaseOrdemServico(tempoGasto, Status.ENTREGUE.toString());
        } catch (Exception e) {
            logger.error("Erro ao atualizar métrica de tempo gasto para Ordem de Serviço ID {}: {}", ordemServico.getId(), e.getMessage(), e);
        }
    }
}