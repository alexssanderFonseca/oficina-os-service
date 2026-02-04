package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoClienteNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoItemNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoServicoNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoVeiculoNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.AbrirOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.*;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.input.CriarOrdemServicoInput;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class AbrirOrdemServicoUseCaseImpl implements AbrirOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoPecaInsumoPort ordemServicoPecaPort;
    private final OrdemServicoServicoPort ordemServicoServicoPort;
    private final OrdemServicoClientePort ordemServicoClientePort;
    private final OrdemServicoMetricPort ordemServicoMetricPort;

    @Override
    public UUID executar(CriarOrdemServicoInput input) {
        var cliente = ordemServicoClientePort.buscarCliente(input.clienteId())
                .orElseThrow(OrdemServicoClienteNaoEncontradoException::new);
        validaDados(cliente, input);
        var ordemServico = abrirOrdemServico(cliente, input);
        ordemServicoRepository.salvar(ordemServico);
        ordemServicoMetricPort.incrementaNumeroOrdensCriadas();
        return ordemServico.getId();
    }

    private void validaDados(Cliente cliente, CriarOrdemServicoInput input) {
        var isVeiculoValido = cliente.getVeiculos()
                .stream()
                .anyMatch(veiculo -> veiculo.getId().equals(input.veiculoId().toString()));

        if (!isVeiculoValido) {
            throw new OrdemServicoVeiculoNaoEncontradoException();
        }

    }

    private OrdemServico abrirOrdemServico(Cliente cliente, CriarOrdemServicoInput input) {
        if (possuitensEServicos(input)) {
            return abrirOrdemServicoParaExecucao(cliente, input);
        }

        return abrirOrdemServicoParaDiagnostico(cliente, input.veiculoId());
    }

    private boolean possuitensEServicos(CriarOrdemServicoInput input) {
        var possuiServicos = Optional.ofNullable(input.servicos())
                .filter(servicos -> !servicos.isEmpty())
                .isPresent();

        var possuiItens = Optional.ofNullable(input.pecasInsumos())
                .filter(servicos -> !servicos.isEmpty())
                .isPresent();

        return possuiServicos && possuiItens;

    }

    private OrdemServico abrirOrdemServicoParaDiagnostico(Cliente cliente,
                                                          UUID veiculoId) {
        return OrdemServico.criarOrdemServicoParaDiagnostico(cliente, veiculoId);
    }

    private OrdemServico abrirOrdemServicoParaExecucao(Cliente cliente, CriarOrdemServicoInput input) {
        return OrdemServico.criarOrdemServicoParaExecucao(
                cliente,
                input.veiculoId(),
                montarItensPecaInsumoNecessariosOs(input.pecasInsumos()),
                montarItensServicosNecessariosOs(input.servicos())
        );
    }

    private List<ItemPecaOrdemServico> montarItensPecaInsumoNecessariosOs(List<CriarOrdemServicoInput.CriarOrdemServicoItemInsumoInput> pecasInsumos) {
        return pecasInsumos
                .stream()
                .map(pecaInsumo -> {
                    var pecaInsumoCompleto = buscarPeca(pecaInsumo.idPecaInsumo());
                    return new ItemPecaOrdemServico(
                            UUID.randomUUID(),
                            pecaInsumoCompleto.id(),
                            pecaInsumoCompleto.nome(),
                            pecaInsumoCompleto.precoVenda(),
                            pecaInsumoCompleto.descricao(),
                            pecaInsumo.qtd());
                }).toList();

    }

    private List<ItemServicoOrdemServico> montarItensServicosNecessariosOs(List<UUID> servicosIds) {
        return servicosIds
                .stream()
                .map(idServico -> {
                    var servico = buscarServico(idServico);
                    return new ItemServicoOrdemServico(UUID.randomUUID(),
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


}
