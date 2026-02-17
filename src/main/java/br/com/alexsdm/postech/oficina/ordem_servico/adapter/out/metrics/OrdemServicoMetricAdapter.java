package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.metrics;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrdemServicoMetricAdapter implements OrdemServicoMetricPort {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoMetricAdapter.class);

    private final MeterRegistry meterRegistry;

    @Override
    public void incrementaNumeroOrdensCriadas() {
        logger.info("Incrementando contador de ordens de serviço criadas.");
        try {
            Counter.builder("ordem_servico.criada.total")
                    .description("Total de Ordens de Serviço criadas")
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            logger.error("Erro ao incrementar contador de ordens de serviço criadas: {}", e.getMessage(), e);
        }
    }

    @Override
    public void registrarTempoGastoFaseOrdemServico(long tempoGasto, String fase) {
        logger.info("Registrando tempo gasto de {}ms na fase '{}' da ordem de serviço.", tempoGasto, fase);
        try {
            Timer.builder("ordem_servico.fase.duracao")
                    .description("Tempo gasto em cada fase da ordem de serviço")
                    .tag("fase", fase)
                    .register(meterRegistry)
                    .record(tempoGasto, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Erro ao registrar tempo gasto na fase '{}' da ordem de serviço: {}", fase, e.getMessage(), e);
        }
    }
}