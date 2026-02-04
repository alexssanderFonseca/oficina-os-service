package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.IniciarDiagnosticoOrdemServicoUsecase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Named
@RequiredArgsConstructor
public class IniciarDiagnosticoOrdemServicoUsecaseImpl implements IniciarDiagnosticoOrdemServicoUsecase {

    private final OrdemServicoRepository repository;

    @Override
    public void executar(UUID osId) {
        var ordemServico = repository.buscarPorId(osId)
                .orElseThrow(OrdemServicoNaoEncontradaException::new);
        ordemServico.iniciarDiagnostico();
        repository.salvar(ordemServico);

    }
}
