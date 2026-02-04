package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Status;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.EntregarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregarOrdemServicoUseCaseImpl implements EntregarOrdemServicoUseCase {

    private final OrdemServicoRepository gateway;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public void executar(UUID id) {
        var os = gateway.buscarPorId(id)
                .orElseThrow(OrdemServicoNaoEncontradaException::new);
        os.entregar();
        atualizarMetricaTempoGasto(os);
        gateway.salvar(os);
    }

    private void atualizarMetricaTempoGasto(OrdemServico ordemServico) {
        var tempoGasto = Duration.between(ordemServico.getDataFinalizacao(), ordemServico.getDataEntrega())
                .toMillis();
        ordemServicoMetricPort.registrarTempoGastoFaseOrdemServico(tempoGasto, Status.ENTREGUE.toString());
    }
}