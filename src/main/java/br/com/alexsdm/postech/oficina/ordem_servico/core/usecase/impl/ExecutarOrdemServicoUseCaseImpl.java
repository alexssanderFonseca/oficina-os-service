package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemPecaOrcamento;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemPecaOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemServicoOrcamento;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.ItemServicoOrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoOrcamentoNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ExecutarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class ExecutarOrdemServicoUseCaseImpl implements ExecutarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoOrcamentoPort orcamentoGateway;

    @Override
    public void executar(UUID osId, UUID orcamentoId) {
        var ordemServico = ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        var orcamento = orcamentoGateway.buscarPorId(orcamentoId)
                .orElseThrow(OrdemServicoOrcamentoNaoEncontradoException::new);

        if (!"ACEITO".equals(orcamento.getStatus())) {
            throw new OrdemServicoException("Orcamento nao está no status ACEITO");
        }

        ordemServico.executar(mapearPecasInsumosParaOrdemServico(orcamento.getPecas()),
                mapearServicosParaOrdemServico(orcamento.getServicos()));

        ordemServicoRepository.salvar(ordemServico);
    }

    private List<ItemPecaOrdemServico> mapearPecasInsumosParaOrdemServico(List<ItemPecaOrcamento> itemPecasInsumoOrcamento) {
        return itemPecasInsumoOrcamento
                .stream()
                .map(itemPecaOrcamento ->
                        new ItemPecaOrdemServico(
                                UUID.randomUUID(),
                                itemPecaOrcamento.getPecaId(),
                                itemPecaOrcamento.getNome(),
                                itemPecaOrcamento.getPreco(),
                                itemPecaOrcamento.getDescricao(),
                                itemPecaOrcamento.getQuantidade()))
                .toList();
    }

    private List<ItemServicoOrdemServico> mapearServicosParaOrdemServico(List<ItemServicoOrcamento> itensServicoOrcamento) {
        return itensServicoOrcamento
                .stream()
                .map(itemServicoOrcamento ->
                        new ItemServicoOrdemServico(UUID.randomUUID(),
                                itemServicoOrcamento.getServicoId(),
                                itemServicoOrcamento.getNome(),
                                itemServicoOrcamento.getDescricao(),
                                itemServicoOrcamento.getPreco()))
                .toList();
    }
}