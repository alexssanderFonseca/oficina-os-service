package br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.impl;

import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoClienteNaoEncontradoException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.domain.exception.OrdemServicoNaoEncontradaException;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.in.BuscarOrdemServicoPorIdUseCase;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoClientePort;
import br.com.alexsdm.postech.oficina.ordem_servico.core.port.out.OrdemServicoRepository;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoDadosClientOutput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoDadosVeiculoOutput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoItemOutput;
import br.com.alexsdm.postech.oficina.ordem_servico.core.usecase.output.BuscarOrdemServicoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarOrdemServicoPorIdUseCaseImpl implements BuscarOrdemServicoPorIdUseCase {

    private final OrdemServicoRepository gateway;
    private final OrdemServicoClientePort ordemServicoClientePort;

    @Override
    public BuscarOrdemServicoOutput executar(UUID id) {
        var ordemServico = gateway.buscarPorId(id)
                .orElseThrow(OrdemServicoNaoEncontradaException::new);

        var cliente = ordemServicoClientePort.buscarCliente(ordemServico.getCliente().getId()).
                orElseThrow(OrdemServicoClienteNaoEncontradoException::new);

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

        return new BuscarOrdemServicoOutput(
                ordemServico.getId(),
                ordemServico.getDataCriacao(),
                ordemServico.getDataInicioDaExecucao(),
                ordemServico.getDataFinalizacao(),
                ordemServico.getDataEntrega(),
                ordemServico.getStatus().toString(),
                dadosCliente,
                dadosVeiculo,
                itens
        );
    }
}