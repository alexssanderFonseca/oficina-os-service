package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoItemNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoServicoNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.FinalizarDiagnosticoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.FinalizarDiagnosticoInput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.FinalizarDiagnosticoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinalizarDiagnosticoUseCaseImpl implements FinalizarDiagnosticoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoOrcamentoPort ordemServicoOrcamentoPort;
    private final OrdemServicoPecaInsumoPort ordemServicoPecaPort;
    private final OrdemServicoServicoPort ordemServicoServicoPort;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public FinalizarDiagnosticoOutput executar(FinalizarDiagnosticoInput input) {
        var ordemServico = ordemServicoRepository.buscarPorId(input.osId())
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        var orcamento = new Orcamento(
                UUID.randomUUID(),
                ordemServico.getCliente().getId(),
                ordemServico.getVeiculoId(),
                null,
                montarItensServicosNecessariosOrcamento(input.servicosIds()),
                montarItensPecaInsumoNecessariosOrcamento(input.pecas())
        );

        var orcamentoId = ordemServicoOrcamentoPort.criar(orcamento);
        ordemServico.finalizarDiagnostico();
        atualizarMetricaTempoGasto(ordemServico);
        return new FinalizarDiagnosticoOutput(orcamentoId);
    }

    private List<ItemPecaOrcamento> montarItensPecaInsumoNecessariosOrcamento(List<FinalizarDiagnosticoInput.FinalizarDiagnosticoItemPecaInput> pecasInsumos) {
        return pecasInsumos
                .stream()
                .map(pecaInsumo -> {
                    var pecaInsumoCompleto = buscarPeca(pecaInsumo.pecaId());
                    return new ItemPecaOrcamento(pecaInsumoCompleto.id(),
                            pecaInsumo.quantidade(),
                            pecaInsumoCompleto.nome(),
                            pecaInsumoCompleto.descricao(),
                            pecaInsumoCompleto.precoVenda());
                }).toList();

    }

    private List<ItemServicoOrcamento> montarItensServicosNecessariosOrcamento(List<UUID> servicosIds) {
        return servicosIds
                .stream()
                .map(idServico -> {
                    var servico = buscarServico(idServico);
                    return new ItemServicoOrcamento(UUID.randomUUID(),
                            servico.id(),
                            servico.nome(),
                            servico.descricao(),
                            servico.preco());
                }).toList();

    }

    private PecaInsumo buscarPeca(UUID pecaId) {
        return ordemServicoPecaPort.buscarPecaInsumo(pecaId)
                .orElseThrow(() -> new OrdemServicoItemNaoEncontradoException(pecaId));
    }

    private Servico buscarServico(UUID servicoId) {
        return ordemServicoServicoPort.buscarServicoPorId(servicoId)
                .orElseThrow(() -> new OrdemServicoServicoNaoEncontradoException(servicoId));
    }

    private void atualizarMetricaTempoGasto(OrdemServico ordemServico) {
        var tempoGasto = Duration.between(ordemServico.getDataInicioDiagnostico(), ordemServico.getDataFimDiagnostico())
                .toMillis();
        ordemServicoMetricPort.registrarTempoGastoFaseOrdemServico(tempoGasto, Status.EM_DIAGNOSTICO.toString());

    }
}

