package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.IniciarDiagnosticoOrdemServicoUsecase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Named
@RequiredArgsConstructor
public class IniciarDiagnosticoOrdemServicoUsecaseImpl implements IniciarDiagnosticoOrdemServicoUsecase {

    private static final Logger logger = LoggerFactory.getLogger(IniciarDiagnosticoOrdemServicoUsecaseImpl.class);

    private final OrdemServicoRepository repository;

    @Override
    public void executar(UUID osId) {
        logger.info("Iniciando diagnóstico para Ordem de Serviço ID: {}.", osId);
        try {
            var ordemServico = repository.buscarPorId(osId)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada para iniciar diagnóstico.", osId);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço ID {} encontrada para iniciar diagnóstico.", osId);

            ordemServico.iniciarDiagnostico();
            repository.salvar(ordemServico);
            logger.info("Diagnóstico da Ordem de Serviço ID {} iniciado e salvo com sucesso.", osId);
        } catch (Exception e) {
            logger.error("Erro ao iniciar diagnóstico para Ordem de Serviço ID {}: {}", osId, e.getMessage(), e);
            throw e;
        }
    }
}
