package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.domain.entity.OrdemServico;
import br.com.oficina.ordem_servico.core.domain.entity.TipoItem;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoCatalogoPort;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.oficina.ordem_servico.core.usecase.dto.ItemParaBaixaEstoque;
import br.com.oficina.ordem_servico.core.usecase.dto.events.PagamentoEventoDTO;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequiredArgsConstructor
public class ProcessarRetornoPagamentoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessarRetornoPagamentoUseCase.class);

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoCatalogoPort ordemServicoCatalogoPort;

    public void execute(PagamentoEventoDTO pagamentoEvento) {
        logger.info("Processando evento de pagamento para OS ID: {}", pagamentoEvento.getExternalReference());

        var ordemServico = ordemServicoRepository.buscarPorId(pagamentoEvento.getExternalReference())
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        if ("APROVADO".equalsIgnoreCase(pagamentoEvento.getStatus())) {
            ordemServico.confirmarPagamento(pagamentoEvento.getPagamentoId());
            logger.info("Pagamento aprovado para OS ID: {}. ID do Pagamento: {}", ordemServico.getId(), pagamentoEvento.getPagamentoId());
        } else if ("RECUSADO".equalsIgnoreCase(pagamentoEvento.getStatus())) {
            ordemServico.rejeitarPagamento();
            logger.warn("Pagamento recusado para OS ID: {}. Realizando estorno de estoque.", ordemServico.getId());
            reporItensEstoque(ordemServico);
        } else {
            logger.warn("Status de pagamento desconhecido para OS ID {}: {}", ordemServico.getId(), pagamentoEvento.getStatus());
            // Pode-se considerar um status para 'Falha de Processamento' na OS aqui.
            throw new OrdemServicoException("Status de pagamento desconhecido.");
        }
        ordemServicoRepository.salvar(ordemServico);
        logger.info("Ordem de Serviço ID {} atualizada com status de pagamento: {}", ordemServico.getId(), ordemServico.getStatusPagamento());
    }

    private void reporItensEstoque(OrdemServico ordemServico) {
        var pecasParaRepor = ordemServico.getItens().stream()
                .filter(item -> item.getTipo() == TipoItem.PECA)
                .map(item -> ItemParaBaixaEstoque.builder()
                        .itemId(item.getItemId())
                        .quantidade(item.getQuantidade())
                        .build())
                .toList();

        if (pecasParaRepor.isEmpty()) {
            logger.info("Nenhuma peça identificada para reposição de estoque na OS ID {}.", ordemServico.getId());
            return;
        }
        try {
            logger.info("Realizando reposição de estoque para {} peças da OS ID {}.", pecasParaRepor.size(), ordemServico.getId());
            ordemServicoCatalogoPort.reporEstoque(pecasParaRepor);
            logger.info("Reposição de estoque concluída com sucesso para a OS ID {}.", ordemServico.getId());
        } catch (RuntimeException e) {
            logger.error("Falha ao repor estoque de peças para OS ID {}: {}", ordemServico.getId(), e.getMessage(), e);
            throw new OrdemServicoException("Falha ao repor estoque de peças: " + e.getMessage(), e);
        }
    }
}
