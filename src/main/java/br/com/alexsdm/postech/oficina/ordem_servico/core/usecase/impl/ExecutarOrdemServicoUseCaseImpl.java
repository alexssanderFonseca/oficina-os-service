package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Status;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.TipoItem;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ExecutarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class ExecutarOrdemServicoUseCaseImpl implements ExecutarOrdemServicoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ExecutarOrdemServicoUseCaseImpl.class);

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoCatalogoPort ordemServicoCatalogoPort;

    @Override
    public void executar(UUID osId, UUID orcamentoId) {
        logger.info("Iniciando execução da Ordem de Serviço ID: {} com Orçamento ID: {}.", osId, orcamentoId);
        try {
            var ordemServico = ordemServicoRepository.buscarPorId(osId)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada para execução.", osId);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço ID {} encontrada para execução.", osId);

            if (Status.AGUARDANDO_APROVACAO.equals(ordemServico.getStatus())) {
                logger.warn("Ordem de Serviço ID {} está aguardando aprovação do orçamento.", osId);
                throw new OrdemServicoException("Ordem de servico com aprovação do orçamento pendente");
            }

            baixarItensEstoque(ordemServico);
            ordemServico.executar();
            ordemServicoRepository.salvar(ordemServico);
            logger.info("Ordem de Serviço ID {} executada e salva com sucesso.", osId);
        } catch (Exception e) {
            logger.error("Erro na execução da Ordem de Serviço ID {}: {}", osId, e.getMessage(), e);
            throw e;
        }
    }


    private void baixarItensEstoque(OrdemServico ordemServico) {
        var pecasParaBaixa = ordemServico.getItens().stream()
                .filter(item -> item.getTipo() == TipoItem.PECA)
                .map(item -> ItemParaBaixaEstoque.builder()
                        .itemId(item.getItemId())
                        .quantidade(item.getQuantidade())
                        .build())
                .toList();

        if (pecasParaBaixa.isEmpty()) {
            logger.info("Nenhuma peça identificada para baixa de estoque na OS ID {}.", ordemServico.getId());
            return;
        }
        try {
            logger.info("Realizando baixa de estoque para {} peças da OS ID {}.", pecasParaBaixa.size(), ordemServico.getId());
            ordemServicoCatalogoPort.darBaixaEstoque(pecasParaBaixa);
            logger.info("Baixa de estoque concluída com sucesso para a OS ID {}.", ordemServico.getId());
        } catch (RuntimeException e) {
            logger.error("Falha ao dar baixa no estoque de peças para OS ID {}: {}", ordemServico.getId(), e.getMessage(), e);
            throw new OrdemServicoException("Falha ao dar baixa no estoque de peças: " + e.getMessage(), e);
        }
    }

}