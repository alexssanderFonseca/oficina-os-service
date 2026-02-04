package br.com.alexsdm.postech.oficina.ordem_servico.adapter.out.metrics;

import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoMetricPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrdemServicoMetricAdapter implements OrdemServicoMetricPort {

    private final MeterRegistry meterRegistry;

    @Override
    public void incrementaNumeroOrdensCriadas() {
        Counter.builder("ordem_servico.criada.total")
                .description("Total de Ordens de Serviço criadas")
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void registrarTempoGastoFaseOrdemServico(long tempoGasto, String fase) {
        Timer.builder("ordem_servico.fase.duracao")
                .description("Tempo gasto em cada fase da ordem de serviço")
                .tag("fase", fase)
                .register(meterRegistry)
                .record(tempoGasto, TimeUnit.MILLISECONDS);
    }
}