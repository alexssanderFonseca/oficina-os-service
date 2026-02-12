package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.AprovarOrdemServicoUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Named
@RequiredArgsConstructor
public class AprovarOrdemServicoUseCaseImpl implements AprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public void executar(UUID osId) {
        var ordemServico = ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        ordemServico.aprovar();
        ordemServicoRepository.salvar(ordemServico);
    }
}
