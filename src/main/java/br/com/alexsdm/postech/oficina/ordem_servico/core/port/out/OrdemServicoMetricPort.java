package br.com.alexsdm.postech.oficina.ordem_servico.core.port.out;

public interface OrdemServicoMetricPort {

    void incrementaNumeroOrdensCriadas();

    void registrarTempoGastoFaseOrdemServico(long tempoGasto, String fase);


}
