package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity.Status;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.ExecutarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoOrcamentoPort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

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

        if (Status.AGUARDANDO_APROVACAO.equals(ordemServico.getStatus())) {
            throw new OrdemServicoException("Ordem de servico com aprovação do orçamento pendente");
        }
        ordemServico.executar();
        ordemServicoRepository.salvar(ordemServico);
    }


}