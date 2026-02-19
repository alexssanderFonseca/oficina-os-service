package br.com.oficina.ordem_servico.core.usecase.impl;

import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoClienteNaoEncontradoException;
import br.com.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.oficina.ordem_servico.core.port.in.BuscarOrdemServicoPorIdUseCase;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import br.com.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoDadosClientOutput;
import br.com.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoDadosVeiculoOutput;
import br.com.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoItemOutput;
import br.com.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoOutput;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarOrdemServicoPorIdUseCaseImpl implements BuscarOrdemServicoPorIdUseCase {

    private static final Logger logger = LoggerFactory.getLogger(BuscarOrdemServicoPorIdUseCaseImpl.class);

    private final OrdemServicoRepository gateway;
    private final OrdemServicoClientePort ordemServicoClientePort;

    @Override
    public BuscarOrdemServicoOutput executar(UUID id) {
        logger.info("Iniciando busca de Ordem de Serviço por ID: {}.", id);
        try {
            var ordemServico = gateway.buscarPorId(id)
                    .orElseGet(() -> {
                        logger.warn("Ordem de Serviço com ID {} não encontrada.", id);
                        throw new OrdemServicoNaoEncontradaException();
                    });
            logger.info("Ordem de Serviço com ID {} encontrada.", id);

            var cliente = ordemServicoClientePort.buscarCliente(ordemServico.getCliente().getId()).
                    orElseGet(() -> {
                        logger.warn("Cliente da Ordem de Serviço ID {} não encontrado.", ordemServico.getCliente().getId());
                        throw new OrdemServicoClienteNaoEncontradoException();
                    });

            var veiculo = ordemServico.getVeiculo();
            var dadosVeiculo = new BuscarOrdemServicoDadosVeiculoOutput(veiculo.getPlaca(),
                    veiculo.getMarca(),
                    veiculo.getAno(),
                    veiculo.getCor());

            var itens = ordemServico.getItens().stream()
                    .map(item -> new BuscarOrdemServicoItemOutput(
                            item.getNome(),
                            item.getDescricao(),
                            item.getPreco(),
                            item.getQuantidade(),
                            item.getTipo().name()
                    ))
                    .toList();

            var dadosCliente = new BuscarOrdemServicoDadosClientOutput(cliente.getCpfCnpj());

            logger.info("Detalhes da Ordem de Serviço ID {} processados com sucesso.", id);
            return new BuscarOrdemServicoOutput(
                    ordemServico.getId(),
                    ordemServico.getDataCriacao(),
                    ordemServico.getDataInicioDaExecucao(),
                    ordemServico.getDataFinalizacao(),
                    ordemServico.getDataEntrega(),
                    ordemServico.getStatus().toString(),
                    ordemServico.getStatusPagamento() != null ? ordemServico.getStatusPagamento().toString() : null,
                    dadosCliente,
                    dadosVeiculo,
                    itens
            );
        } catch (Exception e) {
            logger.error("Erro ao buscar Ordem de Serviço por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}